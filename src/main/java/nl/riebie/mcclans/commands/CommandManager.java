package nl.riebie.mcclans.commands;

import nl.riebie.mcclans.ClansImpl;
import nl.riebie.mcclans.api.CommandSender;
import nl.riebie.mcclans.api.enums.Permission;
import nl.riebie.mcclans.commands.FilledParameters.*;
import nl.riebie.mcclans.commands.annotations.*;
import nl.riebie.mcclans.commands.parsers.*;
import nl.riebie.mcclans.commands.constraints.length.LengthConstraint;
import nl.riebie.mcclans.commands.constraints.regex.RegexConstraint;
import nl.riebie.mcclans.messages.Messages;
import nl.riebie.mcclans.player.ClanPlayerImpl;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;


import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by Mirko on 16/01/2016.
 */
public class CommandManager {
    private Map<String, FilledCommand> filledCommandMap = new HashMap<>();
    private Map<CommandSender, FilledCommand> lastExecutedPageCommand = new HashMap<>();
    private Map<CommandSender, Object[]> lastExecutedPageCommandData = new HashMap<>();
    private Map<FilledCommand, Object> commandStructureMap = new HashMap<>();

    private static final Map<Class<?>, ParameterParser<?>> parameterValidatorMap = new HashMap<>();

    static {
        registerParameterValidator(new StringParser(), String.class);
        registerParameterValidator(new IntegerParser(), int.class, Integer.class);
        registerParameterValidator(new DoubleParser(), double.class, Double.class);
        registerParameterValidator(new FloatParser(), float.class, Float.class);
        registerParameterValidator(new BooleanParser(), boolean.class, Boolean.class);
        registerParameterValidator(new PermissionParser(), Permission.class);
    }

    private static void registerParameterValidator(ParameterParser<?> parser, Class<?>... classes) {
        for (Class<?> type : classes) {
            parameterValidatorMap.put(type, parser);
        }
    }

    public void registerCommandStructure(String tag, Class<?> commandStructure) {
        registerCommandStructure(tag, commandStructure, null);
    }

    public void registerCommandStructure(String tag, Class<?> commandStructure, FilledCommand parent) {
        try {
            Object commandStructureInstance = commandStructure.newInstance();
            for (Method method : commandStructure.getMethods()) {
                handleMethod(method, commandStructureInstance, parent);
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void handleMethod(Method method, Object commandStructureInstance, FilledCommand parent) {
        Command commandAnnotation = method.getAnnotation(Command.class);
        if (commandAnnotation != null) {
            FilledCommand filledCommand = new FilledCommand(commandAnnotation.name(), method, commandAnnotation.permission(), commandAnnotation.description());
            commandStructureMap.put(filledCommand, commandStructureInstance);
            if (parent == null) {
                filledCommandMap.put(commandAnnotation.name(), filledCommand);
            } else {
                parent.addChild(filledCommand);
            }
            for (java.lang.reflect.Parameter parameter : method.getParameters()) {
                handleParameter(parameter, filledCommand);
            }
            ChildGroup childGroupAnnotation = method.getAnnotation(ChildGroup.class);
            if (childGroupAnnotation != null) {
                registerCommandStructure("", childGroupAnnotation.value(), filledCommand);
            }
        }
    }

    private void handleParameter(java.lang.reflect.Parameter parameter, FilledCommand filledCommand) {
        if (CommandSender.class.isAssignableFrom(parameter.getType())) {
            filledCommand.addCommandSenderParameter();
        } else if (CommandSource.class.isAssignableFrom(parameter.getType())) {
            filledCommand.addCommandSourceParameter();
        } else {
            handleAnnotatedParameter(parameter, filledCommand);
        }
    }

    private void handleAnnotatedParameter(java.lang.reflect.Parameter parameter, FilledCommand filledCommand) {
        Annotation[] annotations = parameter.getAnnotations();
        if (annotations.length > 0) {
            for (Annotation annotation : annotations) {
                if (annotation instanceof PageParameter) {
                    filledCommand.addPageParameter();
                } else if (annotation instanceof Parameter) {
                    Parameter parameterValues = (Parameter) annotation;

                    Set<Class<?>> validParametersList = parameterValidatorMap.keySet();
                    Multiline multilineParameter = parameter.getAnnotation(Multiline.class);
                    Class<?> listType = null;
                    boolean multiline = false;
                    if (multilineParameter != null) {
                        listType = multilineParameter.listType();
                        multiline = true;
                    }

                    if (!validParametersList.contains(parameter.getType()) && !(multiline && (listType != null || parameter.getType() == String.class))) {     //TODO check if listType is a valid parameter
                        throw new IllegalArgumentException(String.format("Parameter '%s' should be of one of the following types: %s", parameter.getName(), getValidParametersString(validParametersList)));
                    }
                    LengthConstraint lengthConstraint = parameterValues.length();
                    RegexConstraint regexConstraint = parameterValues.regex();


                    filledCommand.addParameter(null, multiline, listType, lengthConstraint.getMinimalLength(),
                            lengthConstraint.getMaximalLength(), regexConstraint.getRegex(), parameter.getType());


                } else if (annotation instanceof OptionalParameter) {
                    OptionalParameter parameterValues = (OptionalParameter) annotation;
                    if (parameter.getType() != Optional.class) {
                        throw new IllegalArgumentException(String.format("Optional parameter '%s' should be of the Optional type", parameter.getName()));
                    }
                    Set<Class<?>> validParametersList = parameterValidatorMap.keySet();
                    Multiline multilineParameter = parameter.getAnnotation(Multiline.class);
                    Class<?> listType = null;
                    boolean multiline = false;
                    if (multilineParameter != null) {
                        listType = multilineParameter.listType();
                        multiline = true;
                    }

                    if (!validParametersList.contains(parameterValues.value()) && !(multiline && (listType != null || parameterValues.value() == String.class))) {
                        throw new IllegalArgumentException(String.format("The generic type of the Optional parameter '%s' should be of one of the following types: %s", parameter.getName(), getValidParametersString(validParametersList)));
                    }
                    LengthConstraint lengthConstraint = parameterValues.length();
                    RegexConstraint regexConstraint = parameterValues.regex();


                    filledCommand.addParameter(parameterValues.value(), multiline, listType, lengthConstraint.getMinimalLength(),
                            lengthConstraint.getMaximalLength(), regexConstraint.getRegex(), parameter.getType());
                }
            }
        } else {
            filledCommand.addParameter(parameter.getType());
        }
    }

    private String getValidParametersString(Set<Class<?>> validParametersList) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean first = true;
        for (Class<?> validType : validParametersList) {
            if (!first) {
                stringBuilder.append(", ");
            }
            stringBuilder.append(validType.getSimpleName());
            first = false;
        }
        return stringBuilder.toString();
    }

    public void executeCommand(CommandSource commandSource, String[] args) {
        CommandSender commandSender = getCommandSender(commandSource);
        String firstParam = args[0];
        int page = 1;
        if (firstParam.equals("page")) {
            FilledCommand filledCommand = lastExecutedPageCommand.get(commandSender);
            Object[] objects = lastExecutedPageCommandData.get(commandSender);
            objects[objects.length - 1] = Integer.valueOf(args[1]);
            filledCommand.execute(commandStructureMap.get(filledCommand), objects);
            return;
        }

        FilledCommand filledCommand = filledCommandMap.get(firstParam);
        Permission permission = filledCommand.getPermission();
        if (permission != Permission.none && !commandSender.checkPermission(permission)) {
            Messages.sendYouDoNotHaveTheRequiredPermission(commandSource, permission.name());
            return;
        }
        if (filledCommand != null) {
            int i;
            for (i = 1; i < args.length; i++) {
                String arg = args[i];
                FilledCommand child = filledCommand.getChild(arg);
                if (child == null) {
                    break;
                } else {
                    filledCommand = child;
                }
            }
            List<FilledParameter> parameters = filledCommand.getParameters();
            Object[] objects = new Object[parameters.size()];
            int argOffset = i;
            for (int j = 0; j < parameters.size(); j++) {
                int index = argOffset + j;
                FilledParameter parameter = parameters.get(j);
                if (parameter instanceof CommandSenderFilledParameter) {
                    objects[j] = commandSender;
                    argOffset--;
                } else if (parameter instanceof CommandSourceFilledParameter) {
                    objects[j] = commandSource;
                    argOffset--;
                } else if (parameter instanceof NormalFilledParameter) {
                    NormalFilledParameter normalFilledParameter = (NormalFilledParameter) parameter;

                    Class<?> type = normalFilledParameter.getParameterType();
                    if (normalFilledParameter.isOptional()) {
                        type = normalFilledParameter.getOptionalType();
                        if (index < args.length) {
                            if (normalFilledParameter.isMultiline()) {
                                Class<?> listType = normalFilledParameter.getListType();
                                if (listType != Void.class) {
                                    ParameterParser<?> parser = parameterValidatorMap.get(listType);
                                    List<Object> parameterList = new ArrayList<>();
                                    for (int pIndex = index; pIndex < args.length; pIndex++) {
                                        ParseResult<?> result = parser.parseValue(args[pIndex], normalFilledParameter);
                                        if (result.isSuccess()) {
                                            parameterList.add(result.getItem());
                                        } else {
                                            commandSender.sendMessage(Text.of(result.getErrorMessage()));
                                            return;
                                        }
                                    }
                                    objects[j] = Optional.of(parameterList);
                                } else {
                                    ParameterParser<String> parser = (ParameterParser<String>) parameterValidatorMap.get(String.class);
                                    StringBuilder stringBuilder = new StringBuilder();
                                    for (int pIndex = index; pIndex < args.length; pIndex++) {
                                        if (pIndex != index) {
                                            stringBuilder.append(" ");
                                        }
                                        stringBuilder.append(args[pIndex]);
                                    }
                                    ParseResult<String> result = parser.parseValue(stringBuilder.toString(), normalFilledParameter);
                                    if (result.isSuccess()) {
                                        objects[j] = Optional.of(result.getItem());
                                    } else {
                                        commandSender.sendMessage(Text.of(result.getErrorMessage()));
                                        return;
                                    }
                                }
                            } else {
                                ParameterParser<?> parser = parameterValidatorMap.get(type);
                                ParseResult<?> parseResult = parser.parseValue(args[index], normalFilledParameter);

                                if (parseResult.isSuccess()) {
                                    objects[j] = Optional.of(parseResult.getItem());
                                } else {
                                    commandSender.sendMessage(Text.of(parseResult.getErrorMessage()));
                                    return;
                                }
                            }
                        } else {
                            objects[j] = Optional.empty();
                        }
                    } else {
                        if (normalFilledParameter.isMultiline()) {
                            Class<?> listType = normalFilledParameter.getListType();
                            if (listType != Void.class) {
                                ParameterParser<?> parser = parameterValidatorMap.get(listType);
                                List<Object> parameterList = new ArrayList<>();
                                for (int pIndex = index; pIndex < args.length; pIndex++) {
                                    ParseResult<?> result = parser.parseValue(args[pIndex], normalFilledParameter);
                                    if (result.isSuccess()) {
                                        parameterList.add(result.getItem());
                                    } else {
                                        commandSender.sendMessage(Text.of(result.getErrorMessage()));
                                        return;
                                    }
                                }
                                objects[j] = parameterList;
                            } else {
                                ParameterParser<String> parser = (ParameterParser<String>) parameterValidatorMap.get(String.class);
                                StringBuilder stringBuilder = new StringBuilder();
                                if (index >= args.length) {
                                    commandSender.sendMessage(Text.of("Parameter should be supplied")); //TODO real error message
                                    return;
                                }
                                for (int pIndex = index; pIndex < args.length; pIndex++) {
                                    if (pIndex != index) {
                                        stringBuilder.append(" ");
                                    }
                                    stringBuilder.append(args[pIndex]);
                                }
                                ParseResult<String> result = parser.parseValue(stringBuilder.toString(), normalFilledParameter);
                                if (result.isSuccess()) {
                                    objects[j] = result.getItem();
                                } else {
                                    commandSender.sendMessage(Text.of(result.getErrorMessage()));
                                    return;
                                }
                            }
                        } else {
                            ParameterParser<?> parser = parameterValidatorMap.get(type);
                            ParseResult<?> parseResult = parser.parseValue(args[index], normalFilledParameter);
                            if (parseResult.isSuccess()) {
                                objects[j] = parseResult.getItem();
                            } else {
                                commandSender.sendMessage(Text.of(parseResult.getErrorMessage()));
                                return;
                            }
                        }
                    }
                } else if (parameter instanceof PageFilledParameter) {
                    lastExecutedPageCommand.put(commandSender, filledCommand);
                    lastExecutedPageCommandData.put(commandSender, objects);
                    if (index < args.length) {
                        IntegerParser integerParser = new IntegerParser();
                        ParseResult<Integer> parseResult = integerParser.parseValue(args[index], new NormalFilledParameter(Integer.class));
                        if (parseResult.isSuccess()) {
                            page = parseResult.getItem();
                        }
                    }
                    objects[j] = page;
                }
            }
            filledCommand.execute(commandStructureMap.get(filledCommand), objects);
        }
    }

    private CommandSender getCommandSender(CommandSource commandSource) {
        Player player = (Player) commandSource;

        UUID uuid = player.getUniqueId();
        ClanPlayerImpl clanPlayer = ClansImpl.getInstance().getClanPlayer(uuid);
        if (clanPlayer == null) {
            clanPlayer = ClansImpl.getInstance().createClanPlayer(uuid, player.getName());
        }
        return clanPlayer;
    }
}

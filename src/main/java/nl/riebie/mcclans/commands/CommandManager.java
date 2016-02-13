package nl.riebie.mcclans.commands;

import nl.riebie.mcclans.ClansImpl;
import nl.riebie.mcclans.api.CommandSender;
import nl.riebie.mcclans.commands.FilledParameters.*;
import nl.riebie.mcclans.commands.annotations.ChildGroup;
import nl.riebie.mcclans.commands.annotations.Command;
import nl.riebie.mcclans.commands.annotations.PageParameter;
import nl.riebie.mcclans.commands.annotations.Parameter;
import nl.riebie.mcclans.commands.validators.*;
import nl.riebie.mcclans.player.ClanPlayerImpl;
import org.spongepowered.api.text.Text;


import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
                    filledCommand.addParameter(parameterValues.optional(), parameterValues.minimalLength(), parameterValues.maximalLength(), parameterValues.regex(), parameter.getType());
                }
            }
        } else {
            filledCommand.addParameter(parameter.getType());
        }
    }

    public void executeCommand(String commandSenderName, String[] args) {
        CommandSender commandSender = getCommandSender(commandSenderName);
        String firstParam = args[0];
        int page = 0;
        if (firstParam.equals("page")) {
            FilledCommand filledCommand = lastExecutedPageCommand.get(commandSender);
            Object[] objects = lastExecutedPageCommandData.get(commandSender);
            objects[objects.length - 1] = Integer.valueOf(args[1]);
            filledCommand.execute(commandStructureMap.get(filledCommand), objects);
            return;
        }

        FilledCommand filledCommand = filledCommandMap.get(firstParam);

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
            for (int j = 0; j < parameters.size(); j++) {
                int index = (i - 1) + j;
                FilledParameter parameter = parameters.get(j);
                if (parameter instanceof CommandSenderFilledParameter) {
                    objects[j] = commandSender;
                } else if (parameter instanceof NormalFilledParameter) {
                    NormalFilledParameter normalFilledParameter = (NormalFilledParameter) parameter;
                    Class<?> type = normalFilledParameter.getParameterType();
                    ParameterParser<?> parser = parameterValidatorMap.get(type);
                    ParseResult<?> parseResult = parser.parseValue(args[index], normalFilledParameter);
                    if (parseResult.isSuccess()) {
                        objects[j] = parseResult.getItem();
                    } else {
                        commandSender.sendMessage(Text.of(parseResult.getErrorMessage()));
                        return;
                    }
                } else if (parameter instanceof PageFilledParameter) {
                    lastExecutedPageCommand.put(commandSender, filledCommand);
                    lastExecutedPageCommandData.put(commandSender, objects);
                    objects[j] = page;
                }
            }
            filledCommand.execute(commandStructureMap.get(filledCommand), objects);
        }
    }

    private CommandSender getCommandSender(String playerName) {
        return ClansImpl.getInstance().getClanPlayer(playerName);
    }
}

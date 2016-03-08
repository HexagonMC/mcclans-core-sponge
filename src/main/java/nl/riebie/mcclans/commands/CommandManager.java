package nl.riebie.mcclans.commands;

import nl.riebie.mcclans.ClansImpl;
import nl.riebie.mcclans.api.Clan;
import nl.riebie.mcclans.api.CommandSender;
import nl.riebie.mcclans.api.enums.Permission;
import nl.riebie.mcclans.clan.ClanImpl;
import nl.riebie.mcclans.commands.FilledParameters.*;
import nl.riebie.mcclans.commands.annotations.*;
import nl.riebie.mcclans.commands.parsers.*;
import nl.riebie.mcclans.commands.constraints.length.LengthConstraint;
import nl.riebie.mcclans.commands.constraints.regex.RegexConstraint;
import nl.riebie.mcclans.config.Config;
import nl.riebie.mcclans.messages.Messages;
import nl.riebie.mcclans.player.ClanPlayerImpl;
import nl.riebie.mcclans.table.HorizontalTable;
import nl.riebie.mcclans.table.TableAdapter;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;


import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Created by Mirko on 16/01/2016.
 */
public class CommandManager {

    private static final int COMMANDS_PER_PAGE = 5;

    private Map<String, FilledCommand> filledCommandMap = new HashMap<>();

    private Map<FilledCommand, Object> commandStructureMap = new HashMap<>();

    //last executed page commands
    private Map<CommandSender, FilledCommand> lastExecutedPageCommand = new HashMap<>();
    private Map<CommandSender, Object[]> lastExecutedPageCommandData = new HashMap<>();

    //parameter validation
    private static final Map<Class<?>, ParameterParser<?>> parameterValidatorMap = new HashMap<>();
    private static final Map<Class<?>, String> parameterDescriptionMap = new HashMap<>();

    static {
        registerParameterValidator(new StringParser(), "word", String.class);
        registerParameterValidator(new IntegerParser(), "number", int.class, Integer.class);
        registerParameterValidator(new DoubleParser(), "decimal number", double.class, Double.class);
        registerParameterValidator(new FloatParser(), "decimal number", float.class, Float.class);
        registerParameterValidator(new BooleanParser(), "value (on/off)", boolean.class, Boolean.class);

        registerParameterValidator(new PermissionParser(), "permission", Permission.class);
        registerParameterValidator(new ToggleParser(), "value (on/off/toggle)", Toggle.class);
        registerParameterValidator(new ClanParser(), "clanTag", Clan.class, ClanImpl.class);
        registerParameterValidator(new TextColorParser(), "color", TextColor.class);
    }

    private static void registerParameterValidator(ParameterParser<?> parser, String userFriendlyDescription, Class<?>... classes) {

        for (Class<?> type : classes) {
            parameterValidatorMap.put(type, parser);
            parameterDescriptionMap.put(type, userFriendlyDescription);
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
            FilledCommand filledCommand = new FilledCommand(commandAnnotation, method, parent == null ? "clan" : parent.getFullPath());
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

                    filledCommand.addParameter(parameterValues.name(), null, multiline, listType, lengthConstraint.getMinimalLength(),
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


                    filledCommand.addParameter(parameterValues.name(), parameterValues.value(), multiline, listType, lengthConstraint.getMinimalLength(),
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

        if (firstParam.equals("page")) {
            FilledCommand filledCommand = lastExecutedPageCommand.get(commandSender);
            int page = Integer.valueOf(args[1]);

            if (filledCommand.hasChildren()) {
                sendContextHelp(commandSender, commandSource, filledCommand, page);
                return;
            } else {
                Object[] objects = lastExecutedPageCommandData.get(commandSender);
                objects[objects.length - 1] = page;
                filledCommand.execute(commandStructureMap.get(filledCommand), objects);
            }
            return;
        } else if (firstParam.equals("help")) {
            int page = args.length > 1 ? Integer.valueOf(args[1]) : 1;
            sendHelp(commandSource, page);
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

            if ((Config.getBoolean(Config.USE_PERMISSIONS) || filledCommand.getSpongePermission().startsWith("mcclans.admin"))
                    && !commandSource.hasPermission(filledCommand.getSpongePermission())) {
                commandSource.sendMessage(Text.of("You're not allowed to execute this command"));
                return;
            }
            if (filledCommand.isPlayerOnly() && !(commandSource instanceof Player)) {
                commandSource.sendMessage(Text.of("This command can only be used by players"));
                return;
            }
            if (filledCommand.isClanOnly() && (!(commandSender instanceof ClanPlayerImpl) || !((ClanPlayerImpl) commandSender).isMemberOfAClan())) {
                commandSource.sendMessage(Text.of("This command can only be used of you are a member of a clan"));
                return;
            }
            Permission permission = filledCommand.getClanPermission();
            if (permission != Permission.none && !commandSender.checkPermission(permission)) {
                Messages.sendYouDoNotHaveTheRequiredPermission(commandSource, permission.name());
                return;
            }
            if (filledCommand.hasChildren()) {
                sendContextHelp(commandSender, commandSource, filledCommand, 1);
                return;
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
                    boolean isOptional = normalFilledParameter.isOptional();
                    if (isOptional) {
                        type = normalFilledParameter.getOptionalType();
                    }
                    if (index >= args.length) {
                        if (isOptional) {
                            objects[j] = Optional.empty();
                            continue;
                        } else {
                            displayParameterHelpPage(commandSource, filledCommand);
//                            commandSender.sendMessage(Messages.getWarningMessage("Parameter should be supplied")); //TODO Sponge real error message
                            return;
                        }
                    }
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
                                    commandSender.sendMessage(Messages.getWarningMessage(result.getErrorMessage()));
                                    return;
                                }
                            }
                            objects[j] = isOptional ? Optional.of(parameterList) : parameterList;
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
                                objects[j] = isOptional ? Optional.of(result.getItem()) : result.getItem();
                            } else {
                                commandSender.sendMessage(Messages.getWarningMessage(result.getErrorMessage()));
                                return;
                            }
                        }
                    } else {
                        ParameterParser<?> parser = parameterValidatorMap.get(type);
                        ParseResult<?> parseResult = parser.parseValue(args[index], normalFilledParameter);
                        if (parseResult.isSuccess()) {
                            objects[j] = isOptional ? Optional.of(parseResult.getItem()) : parseResult.getItem();
                        } else {
                            commandSender.sendMessage(Messages.getWarningMessage(parseResult.getErrorMessage()));
                            return;
                        }
                    }

                } else if (parameter instanceof PageFilledParameter) {
                    lastExecutedPageCommand.put(commandSender, filledCommand);
                    lastExecutedPageCommandData.put(commandSender, objects);
                    objects[j] = 1;
                }
            }
            filledCommand.execute(commandStructureMap.get(filledCommand), objects);
        } else{
            sendHelp(commandSource, 1);
        }
    }

    private void sendHelp(CommandSource commandSource, int page) {
        List<FilledCommand> commands = new ArrayList<>();

        this.filledCommandMap.values().stream().filter(
                filledCommand -> !(Config.getBoolean(Config.USE_PERMISSIONS) || filledCommand.getSpongePermission().startsWith("mcclans.admin"))
                        || commandSource.hasPermission(filledCommand.getSpongePermission())).forEach(filledCommand -> {
            commands.add(filledCommand);
            commands.addAll(getSubCommands(filledCommand));
        });

        sendHelpPage(commands, commandSource, page, "help");
    }

    private void sendContextHelp(CommandSender sender, CommandSource commandSource, FilledCommand root, int page) {
        sendHelpPage(getSubCommands(root), commandSource, page, "page");
        lastExecutedPageCommand.put(sender, root);
    }

    private List<FilledCommand> getSubCommands(FilledCommand filledCommand) {
        List<FilledCommand> commands = new ArrayList<>();
        for (FilledCommand subCommand : filledCommand.getChildren()) {
            commands.add(subCommand);
            commands.addAll(getSubCommands(subCommand));
        }
        return commands;
    }

    private void sendHelpPage(List<FilledCommand> commands, CommandSource commandSender, int page, String pageCommand) {

        int id = page - 1;
        int commandMinIndex = getMinIndexForPage(page);
        int commandMaxIndex = getMaxIndexForPage(page, commands.size());
        int maxPages = getMaxPagesForSize(commands.size());

        if (id < maxPages && id >= 0) {
            commandSender.sendMessage(Text.EMPTY);
            Text header = Text.of(
                    Text.builder("== ").color(TextColors.DARK_GRAY).build(),
                    Text.builder("MC").color(TextColors.DARK_GREEN).build(),
                    Text.builder("Clans").color(TextColors.GREEN).build(),
                    Text.of(" Help Page "),
                    Text.builder(String.valueOf(page)).color(TextColors.GREEN).build(),
                    Text.builder("/").color(TextColors.GRAY).build(),
                    Text.builder(String.valueOf(maxPages)).color(TextColors.GREEN).build(),
                    Text.builder(" ==").color(TextColors.DARK_GRAY).build()
            );

            commandSender.sendMessage(header);
            if (page < maxPages) {
                Text subHeader = Text.of(
                        Text.builder("Type").color(TextColors.GRAY).build(),
                        Text.builder(String.format(" /clan %s %s", pageCommand, page + 1)).color(TextColors.DARK_GREEN),
                        Text.builder(" to read the next page").color(TextColors.GRAY)
                );
                commandSender.sendMessage(subHeader);
            }

            commandSender.sendMessage(Text.EMPTY);

            List<FilledCommand> commandsSubList = commands.subList(commandMinIndex, commandMaxIndex);
            for (FilledCommand baseCommand : commandsSubList) {
                Text.Builder firstMessageLine = Text.builder();
                if (baseCommand.getSpongePermission().startsWith("mcclans.admin")) {
                    firstMessageLine.append(
                            Text.of("["),
                            Text.builder("Admin").color(TextColors.RED).build(),
                            Text.of("] ")
                    );
                }
                firstMessageLine.append(Text.builder().color(TextColors.DARK_GREEN).append(Text.of("/" + baseCommand.getFullPath())).build());
                List<FilledParameter> parameters = baseCommand.getParameters();
                for (FilledParameter parameter : parameters) {
                    if (parameter instanceof NormalFilledParameter) {
                        NormalFilledParameter normalParameter = (NormalFilledParameter) parameter;

                        Text parameterText = Text.builder().onHover(TextActions.showText(getParameterDescription(normalParameter))).append(Text.of(normalParameter.getName())).build();
                        if (normalParameter.isOptional()) {
                            firstMessageLine.append(Text.of(Text.builder().color(TextColors.GREEN).append(Text.of(" {")).build(), parameterText, Text.builder().color(TextColors.GREEN).append(Text.of("}")).build()));
                        } else {
                            firstMessageLine.append(Text.of(Text.builder().color(TextColors.GREEN).append(Text.of(" <")).build(), parameterText, Text.builder().color(TextColors.GREEN).append(Text.of(">")).build()));

                        }
                    }
                }

                Text secondMessageLine = Text.of(
                        Text.of(" "),
                        Text.builder(baseCommand.getDescription()).color(TextColors.GRAY).build()
                );

                commandSender.sendMessage(firstMessageLine.build());
                commandSender.sendMessage(secondMessageLine);
            }
        } else if (maxPages == 0) {
            Messages.sendWarningMessage(commandSender, Messages.THIS_COMMAND_HAS_NO_INFORMATION_TO_DISPLAY);
        } else {
            Messages.sendWarningMessage(commandSender, Messages.PAGE_DOES_NOT_EXIST);
        }

    }

    private Text getParameterDescription(NormalFilledParameter parameter) {
        Type type = parameter.getParameterType();
        String prefix = String.format("a %s", parameterDescriptionMap.get(type));
        if (parameter.isMultiline()) {
            if (parameter.getListType() != Void.class) {
                type = parameter.getListType();
                prefix = String.format("a %s list", parameterDescriptionMap.get(type));
            } else {
                prefix = "multiple words";
            }

        } else if (parameter.isOptional()) {
            type = parameter.getOptionalType();
            prefix = String.format("a %s", parameterDescriptionMap.get(type));
        }
        int minimalLength = parameter.getMinimalLength();
        int maximalLength = parameter.getMaximalLength();
        if (minimalLength != -1 && maximalLength != -1) {
            prefix += "; " + minimalLength + "-" + maximalLength;
        }
        if (parameter.isOptional()) {
            prefix += "; optional";
        }


        return Text.of(prefix);
    }

    private int getMinIndexForPage(int page) {
        return ((page * COMMANDS_PER_PAGE) - COMMANDS_PER_PAGE);
    }

    private int getMaxIndexForPage(int page, int size) {
        int commandMaxIndex = (page * COMMANDS_PER_PAGE);
        if (commandMaxIndex > size) {
            commandMaxIndex = size;
        }
        return commandMaxIndex;
    }

    private int getMaxPagesForSize(int amountOfCommands) {
        int pages = amountOfCommands / COMMANDS_PER_PAGE;
        if (amountOfCommands % COMMANDS_PER_PAGE != 0) {
            pages++;
        }
        return pages;
    }

    private void displayParameterHelpPage(CommandSource commandSource, FilledCommand filledCommand) {
        commandSource.sendMessage(Text.EMPTY);
        commandSource.sendMessage(Text.builder("Failed to execute command").color(TextColors.RED).build());
        commandSource.sendMessage(Text.builder("This command requires the following parameters:").color(TextColors.RED).build());
        Text title = Text.of(
                Text.builder("MC").color(TextColors.DARK_GREEN).build(),
                Text.builder("Clans").color(TextColors.GREEN).build(),
                Text.of(" Parameter Help Page")
                );
        HorizontalTable<NormalFilledParameter> table = new HorizontalTable<>(title, 5, (TableAdapter<NormalFilledParameter>) (row, parameter, index) -> {
            Text parameterName;
            if (parameter.isOptional()) {
                parameterName = Text.builder(String.format("{%s}", parameter.getName())).build();
            } else {
                parameterName = Text.builder(String.format("<%s>", parameter.getName())).build();
            }
            Text parameterDescription = getParameterDescription(parameter);

            row.setValue("Parameter", parameterName);
            row.setValue("Description", parameterDescription);

        });
        table.defineColumn("Parameter", 25);
        table.defineColumn("Description", 30);

        Text.Builder fullCommandString = Text.builder("/" + filledCommand.getFullPath());

        List<FilledParameter> requiredParameters = filledCommand.getParameters();
        List<NormalFilledParameter> normalParameters = new ArrayList<>();

        for (FilledParameter parameter : requiredParameters) {
            if (parameter instanceof NormalFilledParameter) {
                NormalFilledParameter normalFilledParameter = (NormalFilledParameter) parameter;
                normalParameters.add(normalFilledParameter);
                if (normalFilledParameter.isOptional()) {
                    fullCommandString.append(Text.builder(" {" + normalFilledParameter.getName() + "}").color(TextColors.GREEN).build());
                } else {
                    fullCommandString.append(Text.builder(" <" + normalFilledParameter.getName() + ">").color(TextColors.GREEN).build());
                }
            }
        }

        table.setMessage(fullCommandString.build());

        table.draw(normalParameters, 1, commandSource);
    }

    private CommandSender getCommandSender(CommandSource commandSource) {

        if (commandSource instanceof Player) {
            Player player = (Player) commandSource;

            UUID uuid = player.getUniqueId();
            ClanPlayerImpl clanPlayer = ClansImpl.getInstance().getClanPlayer(uuid);
            if (clanPlayer == null) {
                clanPlayer = ClansImpl.getInstance().createClanPlayer(uuid, player.getName());
            }

            return clanPlayer;
        } else if (commandSource instanceof ConsoleSource) {
            ConsoleSource consoleSource = (ConsoleSource) commandSource;
            return new ConsoleSender(consoleSource);
        }
        return null;
    }
}

/*
 * Copyright (c) 2016 riebie, Kippers <https://bitbucket.org/Kippers/mcclans-core-sponge>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package nl.riebie.mcclans.commands;

import nl.riebie.mcclans.ClansImpl;
import nl.riebie.mcclans.api.Clan;
import nl.riebie.mcclans.api.ClanPlayer;
import nl.riebie.mcclans.api.permissions.ClanPermission;
import nl.riebie.mcclans.clan.ClanImpl;
import nl.riebie.mcclans.commands.annotations.*;
import nl.riebie.mcclans.commands.constraints.ParameterConstraint;
import nl.riebie.mcclans.commands.filledparameters.*;
import nl.riebie.mcclans.commands.parsers.*;
import nl.riebie.mcclans.config.Config;
import nl.riebie.mcclans.messages.Messages;
import nl.riebie.mcclans.player.ClanPlayerImpl;
import nl.riebie.mcclans.player.CommandSender;
import nl.riebie.mcclans.table.HorizontalTable;
import nl.riebie.mcclans.table.TableAdapter;
import nl.riebie.mcclans.utils.ClassUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.api.command.CommandSource;
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
 * Created by riebie on 16/01/2016.
 */
public class CommandManager {

    private static final int COMMANDS_PER_PAGE = 5;

    private Map<String, String[]> aliasesMap = new HashMap<>();

    private Map<String, FilledCommand> filledCommandMap = new HashMap<>();

    private Map<FilledCommand, Object> commandStructureMap = new HashMap<>();

    private String rootValue;

    //last executed page commands
    private Map<CommandSender, FilledCommand> lastExecutedPageCommand = new HashMap<>();
    private Map<CommandSender, Object[]> lastExecutedPageCommandData = new HashMap<>();

    //parameter validation
    private static final Map<Class<?>, ParameterParser<?>> parameterValidatorMap = new HashMap<>();

    static {
        registerParameterValidator(new StringParser(), String.class);
        registerParameterValidator(new IntegerParser(), int.class, Integer.class);
        registerParameterValidator(new DoubleParser(), double.class, Double.class);
        registerParameterValidator(new FloatParser(), float.class, Float.class);
        registerParameterValidator(new BooleanParser(), boolean.class, Boolean.class);

        registerParameterValidator(new PermissionParser(), ClanPermission.class);
        registerParameterValidator(new ToggleParser(), Toggle.class);
        registerParameterValidator(new ClanParser(), Clan.class, ClanImpl.class);
        registerParameterValidator(new ClanPlayerParser(), ClanPlayer.class, ClanPlayerImpl.class);
        registerParameterValidator(new TextColorParser(), TextColor.class);
    }

    private static void registerParameterValidator(ParameterParser<?> parser, Class<?>... classes) {
        for (Class<?> type : classes) {
            parameterValidatorMap.put(type, parser);
        }
    }

    private void loadConfig() {
        Map<String, String> aliases = Config.getMap(Config.COMMAND_ALIASES, String.class, String.class);
        for (Map.Entry<String, String> aliasSet : aliases.entrySet()) {
            String alias = aliasSet.getKey().replace("/", "");
            String[] command = aliasSet.getValue().replace("/", "").split(" ");
            aliasesMap.put(alias, ArrayUtils.subarray(command, 1, command.length));
        }
    }

    public List<CommandRoot> registerCommandStructure(String tag, Class<?> commandStructure) {
        loadConfig();
        rootValue = tag;
        registerCommandStructure(commandStructure, null);
        List<CommandRoot> roots = new ArrayList<>();
        roots.add(CommandRoot.newRoot(rootValue, "MCClans", this));
        for (Map.Entry<String, String[]> alias : aliasesMap.entrySet()) {
            CommandRoot commandRoot = CommandRoot.newAllias(alias.getKey(), alias.getValue(), this);
            roots.add(commandRoot);
        }
        return roots;
    }

    private void registerCommandStructure(Class<?> commandStructure, FilledCommand parent) {
        try {
            Object commandStructureInstance = commandStructure.newInstance();
            for (Method method : commandStructure.getMethods()) {
                handleMethod(method, commandStructureInstance, parent);
            }
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void handleMethod(Method method, Object commandStructureInstance, FilledCommand parent) {
        Command commandAnnotation = method.getAnnotation(Command.class);
        Aliases aliasesAnnotation = method.getAnnotation(Aliases.class);
        if (commandAnnotation != null) {
            FilledCommand filledCommand = new FilledCommand(commandAnnotation, method, parent == null ? rootValue : parent.getFullPath());
            commandStructureMap.put(filledCommand, commandStructureInstance);
            if (parent == null) {
                filledCommandMap.put(commandAnnotation.name(), filledCommand);
            } else {
                parent.addChild(filledCommand);
            }
            if (aliasesAnnotation != null) {
                String[] aliases = aliasesAnnotation.value();
                for (String alias : aliases) {
                    String[] aliasPrefix = filledCommand.getFullPath().split(" ");
                    aliasesMap.put(alias, ArrayUtils.subarray(aliasPrefix, 1, aliasPrefix.length));
                }
            }

            for (java.lang.reflect.Parameter parameter : method.getParameters()) {
                handleParameter(parameter, filledCommand);
            }
            ChildGroup childGroupAnnotation = method.getAnnotation(ChildGroup.class);
            if (childGroupAnnotation != null) {
                registerCommandStructure(childGroupAnnotation.value(), filledCommand);
            }
        }
    }

    private void handleParameter(java.lang.reflect.Parameter parameter, FilledCommand filledCommand) {
        if (hasNoParameterAnnotation(parameter) && CommandSender.class.isAssignableFrom(parameter.getType())) {
            filledCommand.addCommandSenderParameter();
        } else if (CommandSource.class.isAssignableFrom(parameter.getType())) {
            filledCommand.addCommandSourceParameter();
        } else {
            handleAnnotatedParameter(parameter, filledCommand);
        }
    }

    private boolean hasNoParameterAnnotation(java.lang.reflect.Parameter parameter) {
        return parameter.getAnnotation(Parameter.class) == null;
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

                    Type optionalType = null;
                    boolean hasOptionalType = parameter.getType().equals(Optional.class);
                    if (hasOptionalType) {
                        optionalType = ClassUtils.getGenericType(parameter.getParameterizedType());
                    }

                    Multiline multilineParameter = parameter.getAnnotation(Multiline.class);
                    Type genericListType;
                    Type parsedType = hasOptionalType ? optionalType : parameter.getParameterizedType();
                    boolean multiline = multilineParameter != null || ClassUtils.getRawType(parsedType) == List.class;
                    boolean isMultilineString = false;
                    if (multiline) {
                        genericListType = ClassUtils.getGenericType(parsedType);
                        isMultilineString = genericListType == Void.class;
                        parsedType = isMultilineString ? parsedType : ClassUtils.getRawType(genericListType);
                    }
                    if (!validParametersList.contains(ClassUtils.getRawType(parsedType))) {
                        throw new IllegalArgumentException(String.format("Parameter '%s' should be of one of the following types: %s", parameter.getName(), getValidParametersString(validParametersList)));
                    }
                    ParameterConstraint constraint = getConstraintFromParameter(parameterValues);

                    filledCommand.addParameter(parameterValues.name(), hasOptionalType, multiline, isMultilineString, constraint, parsedType);


                }
            }
        } else {
            filledCommand.addParameter(parameter.getType());
        }
    }

    private ParameterConstraint getConstraintFromParameter(Parameter parameter) {
        try {
            return parameter.constraint().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException(String.format("Cannot instantiate %s", parameter.constraint().getName()), e);
        }
    }

    private String getValidParametersString(Set<Class<?>> validParametersList) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean first = true;
        for (Type validType : validParametersList) {
            if (!first) {
                stringBuilder.append(", ");
            }
            stringBuilder.append(validType.getTypeName());
            first = false;
        }
        return stringBuilder.toString();
    }

    public void executeCommand(CommandSource commandSource, String root, String[] args) {
        CommandSender commandSender = getCommandSender(commandSource);

        if (!root.equals(rootValue)) {
            String[] aliasPrefix = aliasesMap.get(root);
            args = ArrayUtils.addAll(aliasPrefix, args);
            if (aliasPrefix == null) {
                throw new IllegalStateException(String.format("Unknown root: %s", root));
            }
        }
        if (args.length == 0) {
            sendHelp(commandSource, 1);
            return;
        }
        String firstParam = args[0];
        FilledCommand filledCommand = filledCommandMap.get(firstParam);
        if (firstParam.equals("page")) {
            filledCommand = lastExecutedPageCommand.get(commandSender);
            if (filledCommand == null) {
                Messages.sendWarningMessage(commandSource, Messages.NO_TABLE_TO_BROWSE);
                return;
            }
            ParameterParser parameterParser = parameterValidatorMap.get(Integer.class);
            int page = args.length > 1 ? Integer.valueOf(args[1]) : 1;

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
                Messages.sendWarningMessage(commandSource, Messages.YOU_DO_NOT_HAVE_PERMISSION_TO_USE_THIS_COMMAND);
                return;
            }
            if (filledCommand.isPlayerOnly() && !(commandSource instanceof Player)) {
                Messages.sendWarningMessage(commandSource, Messages.YOU_NEED_TO_BE_A_PLAYER_TO_PERFORM_THIS_COMMAND);
                return;
            }
            if (filledCommand.isClanOnly() && (!(commandSender instanceof ClanPlayerImpl) || !((ClanPlayerImpl) commandSender).isMemberOfAClan())) {
                Messages.sendWarningMessage(commandSource, Messages.YOU_NEED_TO_BE_A_MEMBER_OF_A_CLAN_TO_PERFORM_THIS_COMMAND);
                return;
            }
            String permission = filledCommand.getClanPermission();
            if (!permission.equals("none") && !commandSender.checkPermission(permission)) {
                Messages.sendYouDoNotHaveTheRequiredPermission(commandSource, permission);
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

                    Type type = normalFilledParameter.getParameterType();
                    boolean isOptional = normalFilledParameter.isOptional();
                    if (index >= args.length) {
                        if (isOptional) {
                            objects[j] = Optional.empty();
                            continue;
                        } else {
                            displayParameterHelpPage(commandSource, filledCommand);
                            return;
                        }
                    }
                    if (normalFilledParameter.isMultiline()) {
                        ParameterConstraint constraint = normalFilledParameter.getConstraint();
                        if (normalFilledParameter.isMultilineString()) {
                            StringBuilder stringBuilder = new StringBuilder();
                            for (int pIndex = index; pIndex < args.length; pIndex++) {
                                String value = args[pIndex];
                                if (!"".equals(constraint.getRegex()) && !value.matches(constraint.getRegex())) {
                                    commandSource.sendMessage(Messages.getWarningMessage(
                                            String.format("Error while executing command: Value should match regex \'%s\'", constraint.getRegex())));
                                    return;
                                }
                                if (pIndex != index) {
                                    stringBuilder.append(" ");
                                }
                                stringBuilder.append(value);
                            }
                            String result = stringBuilder.toString();
                            if (constraint.getMaximalLength() > -1 &&
                                    result.length() > constraint.getMaximalLength()) {
                                commandSource.sendMessage(Messages.getWarningMessage("Supplied parameter is too large"));
                                return;
                            } else if (constraint.getMinimalLength() > -1 &&
                                    result.length() < constraint.getMinimalLength()) {
                                commandSource.sendMessage(Messages.getWarningMessage("The supplied parameter is too small"));
                                return;
                            }
                            objects[j] = isOptional ? Optional.of(result) : result;
                        } else {
                            ParameterParser<?> parser = parameterValidatorMap.get(type);
                            List<Object> parameterList = new ArrayList<>();
                            for (int pIndex = index; pIndex < args.length; pIndex++) {
                                ParseResult<?> result = parser.parseValue(commandSource, args[pIndex], normalFilledParameter);
                                if (result.isSuccess()) {
                                    parameterList.add(result.getItem());
                                } else {
                                    commandSource.sendMessage(Messages.getWarningMessage(String.format("Error while executing command: %s", result.getErrorMessage())));
                                    return;
                                }
                            }
                            objects[j] = isOptional ? Optional.of(parameterList) : parameterList;
                        }
                    } else {
                        ParameterParser<?> parser = parameterValidatorMap.get(type);
                        ParseResult<?> parseResult = parser.parseValue(commandSource, args[index], normalFilledParameter);
                        if (parseResult.isSuccess()) {
                            objects[j] = isOptional ? Optional.of(parseResult.getItem()) : parseResult.getItem();
                        } else {
                            commandSource.sendMessage(Messages.getWarningMessage(parseResult.getErrorMessage()));
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
        } else {
            sendHelp(commandSource, 1);
        }
    }

    private void sendHelp(CommandSource commandSource, int page) {
        List<FilledCommand> commands = new ArrayList<>();

        List<FilledCommand> filledCommands = new ArrayList<>(filledCommandMap.values());
        Collections.sort(filledCommands, (o1, o2) -> {
            boolean adminCommand1 = o1.getSpongePermission().startsWith("mcclans.admin");
            boolean adminCommand2 = o2.getSpongePermission().startsWith("mcclans.admin");

            if (adminCommand1 && adminCommand2) {
                return 0;
            } else if (adminCommand1) {
                return 1;
            } else if (adminCommand2) {
                return -1;
            }

            return o1.getName().compareTo(o2.getName());
        });
        filledCommands.stream().filter(
                filledCommand -> !(Config.getBoolean(Config.USE_PERMISSIONS) || filledCommand.getSpongePermission().startsWith("mcclans.admin"))
                        || commandSource.hasPermission(filledCommand.getSpongePermission())).forEach(filledCommand -> {
            if (filledCommand.hasChildren()) {
                commands.addAll(getSubCommands(filledCommand));
            } else {
                commands.add(filledCommand);
            }
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
            if (subCommand.hasChildren()) {
                commands.addAll(getSubCommands(subCommand));
            } else {
                commands.add(subCommand);
            }
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
        ParameterConstraint constraint = parameter.getConstraint();
        Type type = parameter.getParameterType();
        String description = parameterValidatorMap.get(type).getDescription();

        String prefix = String.format("a %s", description);
        if (parameter.isMultiline()) {
            if (parameter.isMultilineString()) {
                prefix = "multiple words";
            } else {
                prefix = String.format("a %s list", description);
            }

        } else if (parameter.isOptional()) {
            prefix = String.format("a %s", description);
        }
        int minimalLength = constraint.getMinimalLength();
        int maximalLength = constraint.getMaximalLength();
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

        requiredParameters.stream().filter(parameter -> parameter instanceof NormalFilledParameter).forEach(parameter -> {
            NormalFilledParameter normalFilledParameter = (NormalFilledParameter) parameter;
            normalParameters.add(normalFilledParameter);
            if (normalFilledParameter.isOptional()) {
                fullCommandString.append(Text.builder(" {" + normalFilledParameter.getName() + "}").color(TextColors.GREEN).build());
            } else {
                fullCommandString.append(Text.builder(" <" + normalFilledParameter.getName() + ">").color(TextColors.GREEN).build());
            }
        });

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
        }
        return null;
    }
}

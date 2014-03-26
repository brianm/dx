package io.xn.dx.cli;

import io.airlift.command.Cli;
import io.airlift.command.Help;

public class Main
{
    public static void main(String[] args)
    {
        Cli<Runnable> cli = Cli.<Runnable>builder("gdx")
                               .withCommands(Help.class,
                                             ServerCommand.class)
                               .withDefaultCommand(Help.class)
                               .build();
        cli.parse(args).run();
    }
}

package com.precisionhawk.latas.client;

import com.beust.jcommander.JCommander;
import com.precisionhawk.latas.client.run.PropertyBasedSimRunner;
import com.precisionhawk.latas.client.run.RestBasedSimRunner;
import com.precisionhawk.latas.client.run.StompListener;

import static java.lang.System.exit;

public class Main {

    public static void main(String[] args) {
        String cmdName = args[0];
        Runnable cmd = null;
        switch (cmdName) {
            case "property":
                cmd = new PropertyBasedSimRunner();
                break;
            case "rest":
                cmd = new RestBasedSimRunner();
                break;
            case "stomp":
                cmd = new StompListener();
                break;
            default:
                System.out.println("First arg must be 'property', 'rest', or 'stomp'");
                exit(1);
        }
        new JCommander(cmd, args);
        cmd.run();
    }
}

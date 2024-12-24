package models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LanguageModel {
    private Pattern waitPattern = Pattern.compile("^wait\\s+(\\d+)$");

    /**
     * waitが来るまでif文で処理を取り、waitが来たらEntityStatusModelをAddする。
     * splitする必要のないものを先に処理し、splitする必要がある場合はsplitして処理する。
     */

    public void load(List<EntityStatusModel> stateLayers, String command) {
        // TODO: 言語の解析（メジェドさん）
        boolean isMoveLeft = false;
        boolean isMoveRight = false;

        stateLayers.clear();
        List<String> commands = new ArrayList<>(Arrays.asList(command.split("\n")));
        commands.add("wait 0");
        for (String c : commands) {
            c = c.trim();
            Matcher waitMatcher = waitPattern.matcher(c);
            if (c.equals("move left")) {
                isMoveLeft = true;
            } else if (c.equals("move right")) {
                isMoveRight = true;
            } else if (c.equals("jump")) {
                // TODO: jump処理
            } else if (c.equals("stop")) {
                isMoveLeft = false;
                isMoveRight = false;
            } else if (c.equals("hook")) {
                // TODO: hook処理
            } else if (waitMatcher.matches()) {
                int waiting = Integer.parseInt(waitMatcher.group(1));
                stateLayers.add(new EntityStatusModel(waiting, isMoveRight, isMoveLeft));
                isMoveLeft = false;
                isMoveRight = false;
            } else {
                System.out.println("error");
                // TODO: 言語のコンパイルに失敗したときの処理
            }
        }
    }

}
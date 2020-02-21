package com.shark.ctrip;

import lombok.Data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class GitClone {
    public static void main(String[] args) {

        String gitClone = "git clone http://git.dev.sh.ctripcorp.com/corpgovernment-zhongdianjian/%s.git";

        List<String> repos = Arrays.asList(
                "common",
                "organization-manage",
                "redis-handler",
                "corpgovernment-common-logging",
                "corpgovernment-parent",
                "basic-manage",
                "approval-system",
                "train-booking",
                "supplier-system",
                "order-center",
                "gateway",
                "flight-booking",
                "cost-center"
        );

        for (String repo : repos) {
            new Thread(() -> System.out.println(execute(String.format(gitClone, repo)))).start();
        }

    }

    public static ExecResult execute(String cmd){
        StringBuilder stdSb = new StringBuilder();
        StringBuilder errSb = new StringBuilder();
        ExecResult er = new ExecResult();
        try {
            Process exec = Runtime.getRuntime().exec(cmd);
            BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(exec.getInputStream()));
            BufferedReader stderrReader = new BufferedReader(new InputStreamReader(exec.getErrorStream()));
            String line;
            while ((line = stdoutReader.readLine()) != null) {
                stdSb.append(line).append("\\n");
            }
            er.setStd(stdSb.toString());
            while ((line = stderrReader.readLine()) != null) {
                errSb.append(line).append("\\n");
            }
            er.setErr(errSb.toString());
            er.setExitVal(exec.waitFor());
            return er;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return er;
    }

    @Data
    public static class ExecResult {
        String std;
        String err;
        int exitVal;
    }
}

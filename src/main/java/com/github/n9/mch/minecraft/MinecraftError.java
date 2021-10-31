package com.github.n9.mch.minecraft;

public enum MinecraftError {

    STARTING("既に起動中です。"),
    PROCESS_ERROR("プロセスの作成で問題が発生しました。"),
    NOT_FOUND("サーバープロセスが見つかりませんでした。");

    String msg;

    MinecraftError(String s) {
        this.msg = s;
    }

    public String getMsg() {
        return msg;
    }
}

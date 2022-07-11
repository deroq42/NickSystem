package de.deroq.nicksystem.api.models;

/**
 * @author deroq
 * @since 10.07.2022
 */

public abstract class NickUser {

    public String uuid;
    public String name;
    public boolean autoNick;
    public String nickname;

    /* Public constructor due to pojo exceptions. */
    public NickUser(String uuid, String name, boolean autoNick, String nickname) {
        this.uuid = uuid;
        this.name = name;
        this.autoNick = autoNick;
        this.nickname = nickname;
    }

    /* Public constructor due to pojo exceptions. */
    public NickUser() {
    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public boolean isAutoNick() {
        return autoNick;
    }

    public void setAutoNick(boolean autoNick) {
        this.autoNick = autoNick;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}

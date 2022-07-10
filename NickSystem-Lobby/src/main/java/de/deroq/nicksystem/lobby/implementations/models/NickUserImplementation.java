package de.deroq.nicksystem.lobby.implementations.models;

import de.deroq.nicksystem.api.models.NickUser;

public class NickUserImplementation extends NickUser {

    private NickUserImplementation(String uuid, String name, boolean autoNick, String nickname) {
        super(uuid, name, autoNick, nickname);
    }

    /* Public constructor due to pojo exceptions. */
    public NickUserImplementation() {
    }

    public static class builder {

        private String uuid;
        private String name;
        private boolean autoNick;
        private String nickname;

        public builder setUuid(String uuid) {
            this.uuid = uuid;
            return this;
        }

        public builder setName(String name) {
            this.name = name;
            return this;
        }

        public builder setAutoNick(boolean autoNick) {
            this.autoNick = autoNick;
            return this;
        }

        public builder setNickname(String nickname) {
            this.nickname = nickname;
            return this;
        }

        public NickUserImplementation build() {
            return new NickUserImplementation(uuid, name, autoNick, nickname);
        }
    }
}

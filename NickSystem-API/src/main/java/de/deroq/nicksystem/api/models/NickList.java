package de.deroq.nicksystem.api.models;

import java.util.List;

/**
 * @author deroq
 * @since 10.07.2022
 */

public abstract class NickList {

    public List<String> nicknames;
    public List<String> skins;

    /* Public constructor due to pojo exceptions. */
    public NickList(List<String> nicknames, List<String> skins) {
        this.nicknames = nicknames;
        this.skins = skins;
    }

    /* Public constructor due to pojo exceptions. */
    public NickList() {
    }

    public List<String> getNicknames() {
        return nicknames;
    }

    public List<String> getSkins() {
        return skins;
    }
}

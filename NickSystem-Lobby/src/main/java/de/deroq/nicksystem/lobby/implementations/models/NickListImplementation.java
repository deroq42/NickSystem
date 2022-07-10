package de.deroq.nicksystem.lobby.implementations.models;

import de.deroq.nicksystem.api.models.NickList;

import java.util.List;

public class NickListImplementation extends NickList {

    private NickListImplementation(List<String> nicknames, List<String> skins) {
        super(nicknames, skins);
    }

    /* Public constructor due to pojo exceptions. */
    public NickListImplementation() {
    }

    public static NickListImplementation create(List<String> nicknames, List<String> skins) {
        return new NickListImplementation(nicknames, skins);
    }
}

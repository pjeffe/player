package com.mixzing.message.messages;

import java.io.Serializable;

public enum ServerMessageEnum  implements Serializable {

    NEW_LIBRARY,
    PING_ME,
    RECOMMENDATIONS,
    GENRE_BASIS_VECTORS,
    REQUEST_SIGNATURE,
    RESPONSE_DELAYED,
    TAG_RESPONSE,
    TRACK_MAPPING,
    TRACK_EQUIVALENCE,
    FILERESPONSE

}

package net.segoia.ogeg.services.chat.agents;

import net.segoia.event.eventbus.peers.vo.RejectionReason;

public class ChatConstants {
    public static final String CHAT_APP_ID="CHAT";
//    public static final String APP_ID_PARAM="appId";
//    public static final String CHAT_KEY_PARAM="chatKey";
    
    
    /* stream rejection reasons */
    public static final RejectionReason APP_ID_MISSING=new RejectionReason(101, "appId event param missing");
    public static final RejectionReason APP_ID_UNKNOWN=new RejectionReason(102, "appId unknown");
    public static final RejectionReason CHAT_KEY_MISSING=new RejectionReason(103, "chatKey event param missing");
    public static final RejectionReason CHAT_KEY_UNKNOWN=new RejectionReason(104, "chatKey unknown");
    public static final RejectionReason INVALID_CHAT_USER=new RejectionReason(105, "peer is not present in chat");
    
}

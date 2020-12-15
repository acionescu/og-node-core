package net.segoia.ogeg.services.chat.agents;

import net.segoia.event.eventbus.streaming.StreamsManagerConfig;

public class ChatManagerAgentConfig {
    private StreamsManagerConfig streamsManagerConfig=new StreamsManagerConfig();

    public StreamsManagerConfig getStreamsManagerConfig() {
	return streamsManagerConfig;
    }

    public void setStreamsManagerConfig(StreamsManagerConfig streamsManagerConfig) {
	this.streamsManagerConfig = streamsManagerConfig;
    }

}

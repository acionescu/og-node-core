/**
 * og-node-core - The core resources of an Open Groups node
 * Copyright (C) 2020  Adrian Cristian Ionescu - https://github.com/acionescu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.segoia.ogeg.services.status;


/**
 * Keep a view with the last know data for a peer
 * @author adi
 *
 */
public class PeerStatusView {
    private String peerId;
    private String status;
    
    public PeerStatusView(String peerId, String status) {
	super();
	this.peerId = peerId;
	this.status = status;
    }

    /**
     * @return the peerId
     */
    public String getPeerId() {
        return peerId;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    
    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((peerId == null) ? 0 : peerId.hashCode());
	result = prime * result + ((status == null) ? 0 : status.hashCode());
	return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	PeerStatusView other = (PeerStatusView) obj;
	if (peerId == null) {
	    if (other.peerId != null)
		return false;
	} else if (!peerId.equals(other.peerId))
	    return false;
	if (status == null) {
	    if (other.status != null)
		return false;
	} else if (!status.equals(other.status))
	    return false;
	return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("StatusPeerView [");
	if (peerId != null)
	    builder.append("peerId=").append(peerId).append(", ");
	if (status != null)
	    builder.append("status=").append(status);
	builder.append("]");
	return builder.toString();
    }
    
    
}

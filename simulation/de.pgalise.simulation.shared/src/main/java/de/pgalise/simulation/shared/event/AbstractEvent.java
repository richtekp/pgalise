/* 
 * Copyright 2013 PG Alise (http://www.pg-alise.de/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
 
package de.pgalise.simulation.shared.event;

import de.pgalise.simulation.shared.persistence.AbstractIdentifiable;
import java.util.Date;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 * Superclass for every simulation event. It contains a uniqe
 * {@link UUID} and a value of {@link SimulationEventTypeEnum}, this is
 * useful for serializing and deserializing. This class can not be 
 * an interface nor be abstract, because google guice can not handle this.
 * 
 * @author Timo
 */
@ManagedBean
@SessionScoped
public abstract class AbstractEvent extends AbstractIdentifiable implements Event {
	/**
	 * Serial
	 */
	private static final long serialVersionUID = -7362721454716905390L;
	
	private Date commitDateTime;

	protected AbstractEvent() {
		super();
	}
	
	public AbstractEvent(Long id) {
		super(id);
	}

	public void setCommitDateTime(Date commitDateTime) {
		this.commitDateTime = commitDateTime;
	}

	@Override
	public Date getCommitDateTime() {
		return commitDateTime;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		AbstractEvent other = (AbstractEvent) obj;
		if (getType() != other.getType()) {
			return false;
		}
		if (getId() == null) {
			if (other.getId() != null) {
				return false;
			}
		} else if (!getId().equals(other.getId())) {
			return false;
		}
		return true;
	}
}

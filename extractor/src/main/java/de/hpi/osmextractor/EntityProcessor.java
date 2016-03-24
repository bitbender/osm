package de.hpi.osmextractor;

import java.io.Closeable;
import java.util.Map;

import org.openstreetmap.osmosis.core.domain.v0_6.Entity;

public interface EntityProcessor<T extends Entity> extends Closeable {

	void process(T entity, Map<String, String> tags);
	
}

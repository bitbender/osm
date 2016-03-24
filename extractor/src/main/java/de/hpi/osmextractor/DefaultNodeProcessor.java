package de.hpi.osmextractor;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.openstreetmap.osmosis.core.domain.v0_6.Node;

import com.google.gson.Gson;

public class DefaultNodeProcessor extends DefaultEntityProcessor<Node> {

	public DefaultNodeProcessor(File f, Gson gson) {
		super(f, gson);
	}

	@Override
	public void process(Node entity, Map<String, String> tags) {
		HashMap<String, Object> obj = new HashMap<>(tags);
		obj.put("id",			entity.getId());
		obj.put("version",		entity.getVersion());
		obj.put("timestamp",	entity.getTimestamp());
		obj.put("user",			entity.getUser().getId());
		obj.put("changesetId",	entity.getChangesetId());
		obj.put("latitude",		entity.getLatitude());
		obj.put("longitude",	entity.getLongitude());
		writeObject(obj);
	}

}

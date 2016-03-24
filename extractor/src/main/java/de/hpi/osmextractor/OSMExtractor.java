package de.hpi.osmextractor;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.Entity;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.TagCollection;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;
import org.openstreetmap.osmosis.pbf2.v0_6.PbfReader;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

public class OSMExtractor {
	public static void main(String[] args) throws IOException {
		File sourceFile;
		File targetFolder;
		if(args.length>=1)
			sourceFile=new File(args[0]);
		else
			sourceFile=new File("../../Data/osm/germany-2016-03-22.osm.pbf");
		if(args.length>=2)
			targetFolder=new File(args[1]);
		else
			targetFolder=sourceFile.getParentFile();
		new OSMExtractor(sourceFile, targetFolder).extract();
	}
	
	
	private Map<String, EntityProcessor<Node>> nodeProcessors;
	private EntityProcessor<Way> wayProcessor;
	private File sourceFile;
	private Gson gson;
	private String prefix;
	private File targetFolder;
	
	public OSMExtractor(File sourceFile, File targetFolder) {
		this.sourceFile=sourceFile;
		this.targetFolder=targetFolder;
		
		gson = new Gson();		
		prefix=sourceFile.getName().substring(0,sourceFile.getName().length()-8);
		//from http://wiki.openstreetmap.org/wiki/Map_Features
		nodeProcessors = ImmutableMap.<String, EntityProcessor<Node>>builder()
				.put(nodeProcessor("place"))
				.put(nodeProcessor("office"))
				.put(nodeProcessor("shop"))
				.put(nodeProcessor("craft"))
				.put(nodeProcessor("amenity"))
				.put(nodeProcessor("leisure"))
				.put(nodeProcessor("building"))
				.put(nodeProcessor("club"))
				.put(nodeProcessor("sport"))
				.put(nodeProcessor("tourism"))
				.put(nodeProcessor("emergency"))
				.put(nodeProcessor("historic"))
				.build();
		wayProcessor = new DefaultWayProcessor(new File(targetFolder,prefix+"_highway.json"), gson);
	}
	
	private Entry<String, EntityProcessor<Node>> nodeProcessor(String tag) {
		return ImmutablePair.of(tag, new DefaultNodeProcessor(new File(targetFolder,prefix+"_"+tag+".json"), gson));
	}

	public void extract() throws IOException {
		PbfReader reader = new PbfReader(sourceFile, 4);
		reader.setSink(new ProcessingSink());
		reader.run();
		
		for(EntityProcessor<Node> p:nodeProcessors.values())
			p.close();
		wayProcessor.close();
	}
	
	private class ProcessingSink implements Sink {
		@Override
		public void initialize(Map<String, Object> metaData) {}

		@Override
		public void complete() {}

		@Override
		public void release() {}

		@Override
		public void process(EntityContainer entityContainer) {
			Entity entity = entityContainer.getEntity();
			Map<String, String> tags = ((TagCollection)entity.getTags()).buildMap();
			
			if(tags.containsKey("name")) {
				if(entity instanceof Node) {
					for(String key:tags.keySet()) {
						EntityProcessor<Node> proc = nodeProcessors.get(key);
						if(proc!=null)
							proc.process((Node) entity, tags);
					}
				}
				else if(entity instanceof Way) {
					if(tags.containsKey("highway") && tags.containsKey("postal_code"))
						wayProcessor.process((Way) entity, tags);
				}
			}
		}
	}
}

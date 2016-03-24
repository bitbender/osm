package de.hpi.osmextractor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.openstreetmap.osmosis.core.domain.v0_6.Entity;

import com.github.powerlibraries.io.Out;
import com.google.gson.Gson;

public abstract class DefaultEntityProcessor<T extends Entity> implements EntityProcessor<T> {

	private BufferedWriter out;
	private int counter=0;
	private Gson gson;
	private File file;

	public DefaultEntityProcessor(File f, Gson gson) {
		try {
			this.file=f;
			out=Out.file(f).withUTF8().asWriter();
			this.gson=gson;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	protected void writeObject(Map<String, Object> object) {
		try {
			gson.toJson(object, out);
			out.write('\n');
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		counter++;
		if(counter%10000==0)
			System.out.println(this.file.getName()+" writer has written "+counter);
	}
	
	@Override
	public void close() throws IOException {
		out.close();
	}
}

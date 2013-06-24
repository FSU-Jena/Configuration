package edu.fsuj.csb.tools.configuration;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.TreeMap;

import edu.fsuj.csb.tools.xml.ObjectComparator;
import edu.fsuj.csb.tools.xml.Tools;

public class Configuration {
	private String configFileName;
	private static final CharSequence defaultConfigComment = "# This is the config file for the InteractionDB\n#\n#use dbport = 12345 to set database port\n\n";
	private static TreeMap<String,String> config;

	public Configuration(String name) throws IOException {
		configFileName = System.getProperty("user.home")+"/.config/"+name+"/"+name+".config";
		config=null;
		load();
	}
	
	private void load() throws IOException {
		File configFile=new File(configFileName);
		if (!configFile.exists()) {
			Tools.warn("No config file found, creating new config in "+configFileName);
			createDirectory(configFile.getParentFile());
			BufferedWriter bw=new BufferedWriter(new FileWriter(configFile));
			bw.append(defaultConfigComment);
			bw.close();
		}
		BufferedReader br=new BufferedReader(new FileReader(configFile));
		config=new TreeMap<String, String>(ObjectComparator.get());
		while (br.ready()){
			String line=br.readLine();
			int comment=line.indexOf('#');
			if (comment>-1) line=line.substring(0,comment);
			int equal=line.indexOf('=');
			if (equal>1){
				String key=line.substring(0,equal).trim();
				String value=line.substring(equal+1).trim();
				config.put(key, value);
			}
		}
		br.close();
  }
	
	public void writeDefault(String key, String value) throws IOException {
		File configFile=new File(configFileName);
		BufferedWriter bw=null;
		if (!configFile.exists()) {
			Tools.warn("No config file found, creating new config in "+configFileName);
			createDirectory(configFile.getParentFile());
			bw=new BufferedWriter(new FileWriter(configFile));
			bw.append(defaultConfigComment);
		} else {
			bw=new BufferedWriter(new FileWriter(configFile,true));
		}
		bw.write(key+" = "+value+"\n");
		bw.close();
  }
	
	public String get(String key,String defaultValue) throws IOException {
		if (config==null) load();
	  String value=config.get(key);
	  if (value==null){
			writeDefault(key,defaultValue);
			config.put(key, defaultValue);
			value=defaultValue;
	  }
	  return value;
  }
	
	public String get(String key) throws IOException{
		if (config==null) load();
		return config.get(key);
	}
	
	private static void createDirectory(File dir) {
	  if (dir.exists()) return;
	  dir.mkdirs();
  }

	public boolean containsKey(String key) {
	  return config.containsKey(key);
  }
}

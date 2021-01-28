package de.paul.weaponsystem.config;

import java.io.File;
import java.io.IOException;

import org.json.simple.parser.ParseException;

public class WeaponConfig extends Config {

	public WeaponConfig(File f) throws IOException, ParseException {
		super(f);
		
		load();
	}
	
	private void load() {
		
	}
}

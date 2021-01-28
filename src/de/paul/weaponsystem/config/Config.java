package de.paul.weaponsystem.config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Config {
	
	private JSONObject c;
	private File f;

	public Config(File f) throws IOException, ParseException {
		c = readJSON(f);
	}
	
	public Config(JSONObject o) {
		c = o;
	}
	
	public void print(String name) {
		System.out.println(name+": ");
		System.out.println(c.toJSONString());
	}
	
	@Override
	public String toString() {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		return gson.toJson(c);
	}
	
	public String toJSONString() {
		return c.toJSONString();
	}
	
	public void set(String st, Object o) {
		c.put(st.toLowerCase(), o);
		save();
	}
	
	public Config(String string) {
		try {
			JSONParser p = new JSONParser();
			c = (JSONObject) p.parse(string);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	public boolean contains(String s) {
		return c.containsKey(s);
	}
	
	public void save() {
		if (f != null) {
			try {
				BufferedWriter w = new BufferedWriter(new FileWriter(f));
				w.write(toString());
				w.flush();
				w.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public File getFile() {
		return f;
	}
	
	public Object get(String st) {
		return c.get(st.toLowerCase());
	}
	
	public String getChatColorString(String name) {
		return ChatColor.translateAlternateColorCodes('%', (String) get(name));
	}

	private JSONObject readJSON(File f) throws IOException, ParseException {
		FileReader r = new FileReader(f);
		JSONParser p = new JSONParser();
		JSONObject o = (JSONObject) p.parse(r);
		this.f = f;
		return o;
	}

	public Location getLocation(String name) {
		JSONObject ob = (JSONObject) get(name);
		World world = Bukkit.getWorld((String) ob.get("world"));
		double x = (double) ob.get("x");
		double y = (double) ob.get("y");
		double z = (double) ob.get("z");
		double yaw = (double) ob.get("yaw");
		double pitch = (double) ob.get("pitch");
		return new Location(world, x, y, z, (float) yaw, (float) pitch);
	}
	
	public void setLocation(String name, Location loc) {
		JSONObject ob = new JSONObject();
		ob.put("world", loc.getWorld().getName());
		ob.put("x", (double) loc.getX());
		ob.put("y", (double) loc.getY());
		ob.put("z", (double) loc.getZ());
		ob.put("yaw", (double) loc.getYaw());
		ob.put("pitch", (double) loc.getPitch());
		set(name, ob);
	}

	public JSONObject toJSON() {
		return c;
	}

}

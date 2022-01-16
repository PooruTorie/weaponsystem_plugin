package de.paul.weaponsystem.config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.paul.weaponsystem.WeaponSystem;
import de.paul.weaponsystem.assets.Assets;

public class Config {
	
	public JSONObject c;
	private File f;
	private Config p = null;
	private String key;

	public Config(File f) throws IOException, ParseException {
		c = readJSON(f);
	}
	
	public Config(Config o, String subConfigKey) {
		this((JSONObject) o.get(subConfigKey));
		key = subConfigKey;
		p = o;
	}
	
	public Config(JSONObject o) {
		c = o;
		
		if (c == null) {
			c = new JSONObject();
		}
	}
	
	public Config() {
		this(new JSONObject());
	}
	
	public Config(InputStream is) {
		try {
			JSONParser p = new JSONParser();
			JSONObject o = (JSONObject) p.parse(new InputStreamReader(is));
			c = o;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String toString() {
		if (f != null) {
			return "{Config@"+f.getName()+"}";
		}
		if (p != null) {
			return "{Config@"+p.f.getName()+"}";
		}
		return "{Config}";
	}
	
	public String toJSONString() {
		Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
		return gson.toJson(c);
	}
	
	public void set(String st, Object o) {
		if (o instanceof Config) {
			c.put(st, ((Config) o).c);
		} else if (o instanceof ConfigObject) {
			c.put(st, ((ConfigObject)o).finalSave().toJSON());
		} else {
			c.put(st, o);
		}
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
		if (p != null) {
			p.set(key, c);
		}
		if (f != null) {
			BufferedWriter w = null;
			try {
				w = new BufferedWriter(new FileWriter(f, Charset.forName("UTF-8")));
				w.write(toJSONString());
				w.close();
			} catch (IOException e) {
				e.printStackTrace();
				if (w != null) {
					try {
						w.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
	}

	public File getFile() {
		if (p != null) {
			return p.getFile();
		}
		return f;
	}
	
	public Object get(String st) {
		Object v = c.get(st);
		if (v instanceof Long) {
			v = (int) ((Long) v).intValue();
		} else if (v instanceof JSONObject) {
			Config c = new Config((JSONObject) v);
			if (c.contains("CLASS")) {
				return ConfigObject.loader.get(new String(Base64.getDecoder().decode((String) c.get("CLASS")))).load(c);
			}
		}
		return v;
	}
	
	public double getDouble(String st) {
		Object i = get(st);
		if (i instanceof Integer) {
			return new Double((int) i);
		}
		if (i == null) {
			return 0;
		}
		return (double) i;
	}
	
	public void setLong(String st, long i) {
		set(st, ""+i);
	}
	
	public long getLong(String st) {
		Object i = get(st);
		if (i instanceof String) {
			return Long.parseLong((String) i);
		}
		if (i == null) {
			return 0;
		}
		return (long) i;
	}
	
	public String getChatColorString(String name) {
		return ChatColor.translateAlternateColorCodes('&', (String) get(name));
	}
	
	public Config getSubConfig(String key) {
		return new Config(this, key);
	}

	private JSONObject readJSON(File f) throws IOException, ParseException {
		FileReader r = null;
		try {
			r = new FileReader(f, Charset.forName("UTF-8"));
			JSONParser p = new JSONParser();
			JSONObject o = (JSONObject) p.parse(r);
			this.f = f;
			r.close();
			return o;
		} catch (Exception e) {
			if (r != null) {
				try {
					r.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			throw e;
		}
	}

	public Location getLocation(String name) {
		if (contains(name)) {
			if (get(name) instanceof String) {
				return null;
			} else {
				Config ob = getSubConfig(name);
				World world = Bukkit.getWorld((String) ob.get("world"));
				double x = ob.getDouble("x");
				double y = ob.getDouble("y");
				double z = ob.getDouble("z");
				double yaw = ob.getDouble("yaw");
				double pitch = ob.getDouble("pitch");
				return new Location(world, x, y, z, (float) yaw, (float) pitch);
			}
		}
		return null;
	}

	public void setLocation(String name, Location loc) {
		if (loc != null) {
			JSONObject ob = new JSONObject();
			ob.put("world", loc.getWorld().getName());
			ob.put("x", (double) loc.getX());
			ob.put("y", (double) loc.getY());
			ob.put("z", (double) loc.getZ());
			ob.put("yaw", (double) loc.getYaw());
			ob.put("pitch", (double) loc.getPitch());
			set(name, ob);
		} else {
			set(name, "null");
		}
	}

	public JSONObject toJSON() {
		return c;
	}
	
	public void remove(String name) {
		c.remove(name);
		save();
	}
	
	public void setArray(String key, List<?> list) {
		JSONArray array = new JSONArray();
		for (Object object : list) {
			if (object instanceof Config) {
				array.add(((Config) object).c);
			} if (object instanceof ConfigObject) {
				array.add(((ConfigObject)object).finalSave().c);
			} else {
				array.add(object);
			}
		}
		set(key, array);
	}
	
	public void addToArray(String key, Object object) {
		JSONArray array = (JSONArray) get(key);
		if (array == null) {
			array = new JSONArray();
		}
		if (object instanceof Config) {
			array.add(((Config) object).c);
		} else if (object instanceof ConfigObject) {
			array.add(((ConfigObject)object).finalSave().c);
		} else {
			array.add(object);
		}
		set(key, array);
	}
	
	public <E> ArrayList<E> getArray(String key, Class<E> cast) {
		ArrayList<E> list = new ArrayList<>();
		if (contains(key)) {
			for (Object o : (JSONArray) get(key)) {
				E castO;
				if (cast == this.getClass()) {
					JSONObject json = (JSONObject) o;
					castO = (E) new Config(json);
				} else if (ConfigObject.class.isAssignableFrom(cast)) {
					Config c = new Config((JSONObject) o);
					castO = (E) ConfigObject.loader.get(new String(Base64.getDecoder().decode((String) c.get("CLASS")))).load(c);
				} else {
					castO = cast.cast(o);
				}
				list.add(castO);
			}
		}
		return list;
	}
	
	public void clearArray(String key) {
		JSONArray array = (JSONArray) get(key);
		if (array != null) {
			array.clear();
			set(key, array);
		}
	}
	
	public List<String> keys() {
		List<String> keys = new ArrayList<>();
		for (Object o : c.keySet()) {
			if (o instanceof String) {
				keys.add((String) o);
			}
		}
		return keys;
	}
	
	public static Config loadConfig(String name, String... subConfig) {
		File configFile = new File(WeaponSystem.plugin.getDataFolder(), name+".json");
		try {
			Config c = new Config(configFile);
			if (subConfig.length == 0) {
				return c;
			} else {
				for (String subName : subConfig) {
					c = new Config(c, subName);
				}
				return c;
			}
		} catch (IOException e) {
			WeaponSystem.plugin.getDataFolder().mkdirs();
			
			Assets.copyFile(configFile, name+".json");
			
			return loadConfig(name, subConfig);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void clear() {
		c.clear();
		save();
	}
	
	public abstract static class ConfigObject<T extends ConfigObject<T>> {
		
		private static HashMap<String, ConfigObjectLoader> loader = new HashMap<>();
		
		public ConfigObject(Class<T> clazz) {
			loader.put(clazz.getName(), loader());
		}
		
		public abstract Config save();
		
		private Config finalSave() {
			Config c = save();
			c.set("CLASS", new String(Base64.getEncoder().encode(getClass().getName().getBytes())));
			return c;
		}
		
		public abstract ConfigObjectLoader<T> loader();
		
		public interface ConfigObjectLoader<T extends ConfigObject<T>> {
			
			T load(Config c);
			
		}
	}
}

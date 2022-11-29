package client;

import com.beust.jcommander.Parameter;

public class Args {
@Parameter(names = {"--input", "-in"})
private String fileName;
@Parameter(names = {"--type", "-t"})
private String type;
@Parameter(names = {"--key", "-k"})
private String key;
@Parameter(names = {"--value", "-v"})
private String value;

public String getFileName() { return fileName; }

public String getType() { return type; }

public String getKey() { return key; }

public String getValue() { return value; }
}

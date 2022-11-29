package client;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.Parameter;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;


public class Args {

@Parameter(names = {"--input", "-in"})
private String fileName;

@Parameter(names = {"--type", "-t"})
private String type;

@Parameter(names = {"--key", "-k"}, converter = Converter.class)
private JsonElement key;

@Parameter(names = {"--value", "-v"}, converter = Converter.class)
private JsonElement value;


public String getFileName() { return fileName; }

public String getType() { return type; }

public JsonElement getKey() { return key; }

public JsonElement getValue() { return value; }

}

class Converter implements IStringConverter<JsonElement> {
    @Override
    public JsonElement convert(String value) {
        return new JsonPrimitive(value);
    }
}

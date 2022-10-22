package client;

import com.beust.jcommander.Parameter;

import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Stream;

public class Args {

    @Parameter(names = {"--type", "-t"}, required = true)
    private String reqType;
    @Parameter(names = {"--cell", "-i"})
    private String cell;
    @Parameter(names = {"--msg", "-m"})
    private String cellValue;



    public String getRequestString() {
        StringJoiner joiner = new StringJoiner("\s");
        Stream.of(reqType, cell, cellValue).filter(Objects::nonNull).forEach(joiner::add);
        return joiner.toString();
    }
}

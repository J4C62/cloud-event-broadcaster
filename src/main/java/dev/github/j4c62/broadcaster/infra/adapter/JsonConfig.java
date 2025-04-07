package dev.github.j4c62.broadcaster.infra.adapter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class JsonConfig {
  private Map<String, Object> variables;

  public JsonConfig() throws IOException {
    final var file = new File("./src/resource/example.json");
    this.variables = new ObjectMapper().readValue(file, new TypeReference<>() {});
  }
}

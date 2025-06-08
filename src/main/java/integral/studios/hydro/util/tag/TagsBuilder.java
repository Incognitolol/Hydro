package integral.studios.hydro.util.tag;

import java.util.ArrayList;
import java.util.List;

public class TagsBuilder {
    private final List<String> tags = new ArrayList<>();

    public TagsBuilder addTag(String string) {
        tags.add(string);

        return this;
    }

    public String build() {
        return String.join(", ", tags);
    }

    public int getSize() {
        return tags.size();
    }
}
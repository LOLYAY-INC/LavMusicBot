package io.lolyay.embedmakers;

import io.lolyay.lyrics.records.Lyrics;
import net.dv8tion.jda.api.EmbedBuilder;

public class LyricsEmbedGenerator {

    private static final int MAX_FIELD_LENGTH = 1023;
    private static final String INVISIBLE_SEPARATOR = "\u200B";

    public static EmbedBuilder generate(Lyrics lyrics) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(lyrics.query().title() + " by " + lyrics.query().author());
        builder.setFooter(lyrics.source());

        if (lyrics.content() == null || lyrics.content().isEmpty()) {
            builder.setDescription("No lyrics available for this song.");
            return builder;
        }

        String[] lines = lyrics.content().split("\n");
        StringBuilder currentField = new StringBuilder();
        boolean isFirstField = true;

        for (String line : lines) {
            if (line.length() > MAX_FIELD_LENGTH) {
                line = line.substring(0, MAX_FIELD_LENGTH - 3) + "...";
            }

            if (currentField.length() + line.length() + 1 > MAX_FIELD_LENGTH) {
                addLyricsField(builder, currentField.toString(), isFirstField);
                isFirstField = false;
                currentField.setLength(0);
            }

            currentField.append(line).append("\n");
        }

        if (!currentField.isEmpty()) {
            addLyricsField(builder, currentField.toString(), isFirstField);
        }

        return builder;
    }

    private static void addLyricsField(EmbedBuilder builder, String content, boolean isFirst) {
        if (isFirst) {
            builder.addField("", content.trim(), false);
        } else {
            builder.addField(INVISIBLE_SEPARATOR, content.trim(), false);
        }
    }
}
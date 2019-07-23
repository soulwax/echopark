package net.robinfriedli.botify.discord.properties;

import net.robinfriedli.botify.entities.GuildSpecification;
import net.robinfriedli.botify.entities.xml.GuildPropertyContribution;
import net.robinfriedli.botify.exceptions.InvalidPropertyValueException;

public class DefaultSourceProperty extends AbstractGuildProperty {

    public DefaultSourceProperty(GuildPropertyContribution contribution) {
        super(contribution);
    }

    @Override
    public void validate(Object state) {
        String input = (String) state;
        if (!(input.equalsIgnoreCase("spotify") || input.equalsIgnoreCase("youtube"))) {
            throw new InvalidPropertyValueException("Source needs to be either 'spotify' or 'youtube'");
        }
    }

    @Override
    public Object process(String input) {
        return input;
    }

    @Override
    public void setValue(String value, GuildSpecification guildSpecification) {
        guildSpecification.setDefaultSource(value.toUpperCase());
    }

    @Override
    public Object extractPersistedValue(GuildSpecification guildSpecification) {
        return guildSpecification.getDefaultSource();
    }
}

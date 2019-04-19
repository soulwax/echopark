package net.robinfriedli.botify.command.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.robinfriedli.botify.audio.AudioPlayback;
import net.robinfriedli.botify.command.AbstractCommand;
import net.robinfriedli.botify.command.ArgumentContribution;
import net.robinfriedli.botify.command.CommandContext;
import net.robinfriedli.botify.command.CommandManager;
import net.robinfriedli.botify.exceptions.InvalidCommandException;
import net.robinfriedli.botify.util.Util;

public class ForwardCommand extends AbstractCommand {

    public ForwardCommand(CommandContext context, CommandManager commandManager, String commandString, String identifier, String description) {
        super(context, commandManager, commandString, true, identifier, description, Category.PLAYBACK);
    }

    @Override
    public void doRun() {
        AudioPlayback playback = getManager().getAudioManager().getPlaybackForGuild(getContext().getGuild());
        AudioTrack playingTrack = playback.getAudioPlayer().getPlayingTrack();

        if (playingTrack == null) {
            throw new InvalidCommandException("No track is being played at the moment");
        }

        long toForwardMs;
        try {
            if (argumentSet("minutes")) {
                toForwardMs = Integer.parseInt(getCommandBody()) * 60000;
            } else {
                toForwardMs = Integer.parseInt(getCommandBody()) * 1000;
            }
        } catch (NumberFormatException e) {
            throw new InvalidCommandException("'" + getCommandBody() + "' is not convertible to type integer. " +
                "Please enter a valid number.");
        }

        if (toForwardMs <= 0) {
            throw new InvalidCommandException("Expected 1 or greater");
        }

        long newPosition = playback.getCurrentPositionMs() + toForwardMs;
        long duration = playback.getAudioQueue().getCurrent().getDurationMsInterruptible();
        if (newPosition > duration) {
            throw new InvalidCommandException("New position too high! Current track duration: " + Util.normalizeMillis(duration) +
                ", new position: " + Util.normalizeMillis(newPosition));
        }
        playback.setPosition(newPosition);
    }

    @Override
    public void onSuccess() {
        AudioPlayback playback = getManager().getAudioManager().getPlaybackForGuild(getContext().getGuild());
        long currentPositionMs = playback.getCurrentPositionMs();
        sendSuccess(getContext().getChannel(), "Set position to " + Util.normalizeMillis(currentPositionMs));
    }

    @Override
    public ArgumentContribution setupArguments() {
        ArgumentContribution argumentContribution = new ArgumentContribution(this);
        argumentContribution.map("minutes")
            .setDescription("Forward the given amount of minutes.");
        argumentContribution.map("seconds")
            .setDescription("Forward the given amount of seconds. This is default.");
        return argumentContribution;
    }

}

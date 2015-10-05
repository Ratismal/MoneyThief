package io.github.ratismal.moneythief.util;

import java.util.logging.Logger;

import io.github.ratismal.moneythief.MoneyThief;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.Note.Tone;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

public class FanfarePlayer {

	MoneyThief plugin;
	public FanfarePlayer (MoneyThief instance) {
		plugin = instance;
		
	}

	Logger log = MoneyThief.plugin.log;

	FileConfiguration song1;
	FileConfiguration song2;
	FileConfiguration song3;

	public void songOne(Player player) {
		song1 = MoneyThief.plugin.getSongOne();
		song2 = MoneyThief.plugin.getSongTwo();
		song3 = MoneyThief.plugin.getSongThree();
		int i = 0;
		int octave;
		int type;
		String toneS;
		long time;
		String instrum;
		Tone tone;
		Instrument instrument;
		while (song1.get("" + i) != null) {
			//log.info("Note id: " + i);
			octave = song1.getInt(i + ".octave");
			type = song1.getInt(i + ".type");
			toneS = song1.getString(i + ".tone");
			time = song1.getLong(i + ".time");
			instrum = song1.getString(i + ".instrum");
			tone = identifyTone(toneS);
			instrument = identifyInstrum(instrum);
			if (type == 0) {
				natural(player, tone, octave, instrument, time);
			}
			else if (type == 1) {
				sharp(player, tone, octave, instrument, time);
			}
			else if (type == 2) {
				flat(player, tone, octave, instrument, time);
			}
			i++;
		}
	}

	public void songTwo(Player player) {
		song1 = MoneyThief.plugin.getSongOne();
		song2 = MoneyThief.plugin.getSongTwo();
		song3 = MoneyThief.plugin.getSongThree();
		int i = 0;
		int octave;
		int type;
		String toneS;
		long time;
		String instrum;
		Tone tone;
		Instrument instrument;
		while (song2.get("" + i) != null) {
			//log.info("Note id: " + i);
			octave = song2.getInt(i + ".octave");
			type = song2.getInt(i + ".type");
			toneS = song2.getString(i + ".tone");
			time = song2.getLong(i + ".time");
			instrum = song2.getString(i + ".instrum");
			tone = identifyTone(toneS);
			instrument = identifyInstrum(instrum);
			if (type == 0) {
				natural(player, tone, octave, instrument, time);
			}
			else if (type == 1) {
				sharp(player, tone, octave, instrument, time);
			}
			else if (type == 2) {
				flat(player, tone, octave, instrument, time);
			}
			i++;
		}
	}

	public void songThree(Player player) {
		song1 = MoneyThief.plugin.getSongOne();
		song2 = MoneyThief.plugin.getSongTwo();
		song3 = MoneyThief.plugin.getSongThree();
		int i = 0;
		int octave;
		int type;
		String toneS;
		long time;
		String instrum;
		Tone tone;
		Instrument instrument;
		while (song3.get("" + i) != null) {
			//log.info("Note id: " + i);
			octave = song3.getInt(i + ".octave");
			type = song3.getInt(i + ".type");
			toneS = song3.getString(i + ".tone");
			time = song3.getLong(i + ".time");
			instrum = song3.getString(i + ".instrum");
			tone = identifyTone(toneS);
			instrument = identifyInstrum(instrum);
			if (type == 0) {
				natural(player, tone, octave, instrument, time);
			}
			else if (type == 1) {
				sharp(player, tone, octave, instrument, time);
			}
			else if (type == 2) {
				flat(player, tone, octave, instrument, time);
			}
			i++;
		}
	}

	public Instrument identifyInstrum(String instrum) {
		Instrument instrument = null;
		if (instrum.equals("BASS_DRUM")) {
			instrument = Instrument.BASS_DRUM;
		}
		else if (instrum.equals("BASS_GUITAR")) {
			instrument = Instrument.BASS_GUITAR;
		}
		else if (instrum.equals("PIANO")) {
			instrument = Instrument.PIANO;
		}
		else if (instrum.equals("SNARE_DRUM")) {
			instrument = Instrument.SNARE_DRUM;
		}
		else if (instrum.equals("STICKS")) {
			instrument = Instrument.STICKS;
		}
		else {
			log.info("Improper instrument " + instrum + ", defaulting to PIANO");
			instrument = Instrument.PIANO;
		}
		return instrument;
	}

	public Tone identifyTone(String toneS) {
		Tone tone = null;
		if (toneS.equals("A")) {
			tone = Tone.A;
		}
		else if (toneS.equals("B")) {
			tone = Tone.B;
		}
		else if (toneS.equals("C")) {
			tone = Tone.C;
		}
		else if (toneS.equals("D")) {
			tone = Tone.D;
		}
		else if (toneS.equals("E")) {
			tone = Tone.E;
		}
		else if (toneS.equals("F")) {
			tone = Tone.F;
		}
		else if (toneS.equals("G")) {
			tone = Tone.G;
		}
		else {
			log.info("Improper tone " + toneS + ", defaulting to A");
			tone = Tone.A;
		}
		return tone;
	}

	public void natural(Player player, Tone tone, int octave, Instrument instrum, long delay) {
		final Player play = player;
		final Instrument inst = instrum;

		//final Note note = played;
		final Note note = Note.natural(octave, tone);
		final BukkitScheduler scheduler = MoneyThief.plugin.getServer().getScheduler();
		scheduler.scheduleSyncDelayedTask(MoneyThief.plugin, new Runnable() {

			@Override
			public void run() {
				play.playNote(play.getLocation(), inst, note);
			}

		}, delay);
	}

	public void sharp(Player player, Tone tone, int octave, Instrument instrum, long delay) {
		final Player play = player;
		final Instrument inst = instrum;

		//final Note note = played;
		final Note note = Note.sharp(octave, tone);
		final BukkitScheduler scheduler = MoneyThief.plugin.getServer().getScheduler();
		scheduler.scheduleSyncDelayedTask(MoneyThief.plugin, new Runnable() {

			@Override
			public void run() {
				play.playNote(play.getLocation(), inst, note);
			}

		}, delay);
	}

	public void flat(Player player, Tone tone, int octave, Instrument instrum, long delay) {
		final Player play = player;
		final Instrument inst = instrum;
		//final Note note = played;
		final Note note = Note.flat(octave, tone);
		final BukkitScheduler scheduler = MoneyThief.plugin.getServer().getScheduler();
		scheduler.scheduleSyncDelayedTask(MoneyThief.plugin, new Runnable() {

			@Override
			public void run() {
				play.playNote(play.getLocation(), inst, note);
			}

		}, delay);
	}
}

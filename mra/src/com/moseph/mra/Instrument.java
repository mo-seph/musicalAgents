package com.moseph.mra;

//import com.moseph.music.*;
import java.io.Serializable;

/**
 * A Instrument is a unique representation of Musical Agent
 * 
 * @author David Murray-Rust
 * @version $Revision$, $Date$
 */
public class Instrument implements Serializable
{
	public static enum Patch
	{
		// Piano
		AcousticPiano(1, 1.0, 0 ),
		BrightPiano(2),
 ElectricGrandPiano(3),
 HonkytonkPiano(4),
 ElectricPiano1( 5),
 ElectricPiano2(6),
 Harpsichord(7),
 Clavi(8),
		// ChromaticPercussion
		Celesta(9),
 Glockenspiel(10),
 MusicBox(11),
 Vibraphone(12),
 Marimba(13),
 Xylophone( 14),
 TubularBell(15),
 Dulcimer(16),
		// Organ
		DrawbarOrgan(17),
 PercussiveOrgan(18),
 RockOrgan(19),
 Churchorgan(20),
 Reedorgan( 21),
 Accordion(22),
 Harmonica(23),
 TangoAccordion(24),
		// Guitar
		AcousticGuitarnylon(25),
 AcousticGuitarsteel(26),
 ElectricGuitarjazz(27, 0.5, 0), 
ElectricGuitarclean( 28),
ElectricGuitarmuted(29),
 OverdrivenGuitar(30),
 DistortionGuitar( 31),
 Guitarharmonics(32),
		// Bass
		AcousticBass(33, 1.0, -1),
		ElectricBassfinger(34, 1.0, -1),
 ElectricBasspick( 35, 1.0, -1),
 FretlessBass(36, 1.0, -1),
 SlapBass1(37, 1.0, -1),
 SlapBass2( 38, 1.0, -1),
 SynthBass1(39, 1.0, -1),
 SynthBass2(40, 1.0, -1),
		// Strings
		Violin(41), Viola(42), Cello(43), Doublebass(44), TremoloStrings(45), PizzicatoStrings(
				46), OrchestralHarp(47), Timpani(48),
		// Ensemble
		StringEnsemble1(49), StringEnsemble2(50), SynthStrings1(51), SynthStrings2(
				52), VoiceAahs(53), VoiceOohs(54), SynthVoice(55), OrchestraHit(56),
		// Brass
		Trumpet(57), Trombone(58), Tuba(59), MutedTrumpet(60), Frenchhorn(61), BrassSection(
				62), SynthBrass1(63), SynthBrass2(64),
		// Reed
		SopranoSax(65), AltoSax(66), TenorSax(67), BaritoneSax(68), Oboe(69), EnglishHorn(
				70), Bassoon(71), Clarinet(72),
		// Pipe
		Piccolo(73), Flute(74), Recorder(75), PanFlute(76), BlownBottle(77), Shakuhachi(
				78), Whistle(79), Ocarina(80),
		// SynthLead
		Lead1square(81), Lead2sawtooth(82), Lead3calliope(83), Lead4chiff(84), Lead5charang(
				85), Lead6voice(86), Lead7fifths(87), Lead8basslead(88),
		// SynthPad
		Pad1newage(89), Pad2warm(90), Pad3polysynth(91), Pad4choir(92), Pad5bowed(
				93), Pad6metallic(94), Pad7halo(95), Pad8sweep(96),
		// SynthEffects
		FX1rain(97), FX2soundtrack(98), FX3crystal(99), FX4atmosphere(100), FX5brightness(
				101), FX6goblins(102), FX7echoes(103), FX8scifi(104),
		// Ethnic
		Sitar(105), Banjo(106), Shamisen(107), Koto(108), Kalimba(109), Bagpipe(
				110), Fiddle(111), Shanai(112),
		// Percussive
		TinkleBell(113), AgogoBells(114), SteelDrums(115), Woodblock(116), TaikoDrum(
				117), MelodicTom(118), SynthDrum(119), ReverseCymbal(120),
		// Soundeffects
		GuitarFretNoise(121), BreathNoise(122), Seashore(123), BirdTweet(124), TelephoneRing(
				125), Helicopter(126), Applause(127), Gunshot(128);
		public final int program;

		public final double volScale;

		public final int octaveTranspose;

		Patch( int n )
		{
			this( n, 1.0, 0 );
		}

		Patch( int n, double volScale, int octaveTranspose )
		{
			this.program = n;
			this.volScale = volScale;
			this.octaveTranspose = octaveTranspose;
		}
	}

	Patch patch = Patch.AcousticPiano;

	public Instrument( Patch p )
	{
		this.patch = p;
	}

	public Instrument( String name )
	{
		this.patch = patch.valueOf( name );
	}

	public String toString()
	{
		return patch.name();
	}

	public boolean equals( Object o )
	{
		if (!(o instanceof Instrument))
			return false;
		Instrument i = (Instrument) o;
		return patch.equals( i.patch );
	}

	public int getProgramNumber()
	{
		return patch.program;
	}
	
	public double getVolumeScale()
	{
		return patch.volScale;
	}
	
	public int getOctaveTranspose()
	{
		return patch.octaveTranspose;
	}

}

package com.moseph.mra;

//import com.moseph.music.representation.*;
import java.util.List;

public interface MRAConverter
{
	public List<Fragment> fromFile( String filename );
	public void toFile( List<Fragment> fragments, String filename );
}

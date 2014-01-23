package net.javaci.mobile.bomberman.core.util;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;

public class AudioManager {

    private static final String SOUND_PATH ="sound/";

    private static final String DROP_BOMB= SOUND_PATH + "dropBomb.ogg";
    private static final String MAIN_THEME = SOUND_PATH + "mainTheme.ogg";
    private static final String BOOM = SOUND_PATH + "boom.ogg";
    private static final String OPPONENT_DIED= SOUND_PATH + "dying.ogg";
    private static final String JUST_DIED= SOUND_PATH + "justDied.mp3";
    private static final String START_GAME= SOUND_PATH + "levelStart.mp3";


    private AssetManager assetManager;

    //music instances are heavy, we better have only one instance at a time.
    private Music activeMusic;


    public void initialize(){
        this.assetManager = new AssetManager();
        activeMusic = null;

        if ( Gdx.app.getType() == Application.ApplicationType.Android){
            FileHandle directoryHandle = Gdx.files.internal( SOUND_PATH);

            for ( FileHandle file: directoryHandle.list()){
                assetManager.load( file.path(), Sound.class);
            }
        }
        else{   //list() doesn't work for Desktop
            assetManager.load( DROP_BOMB, Sound.class);
            assetManager.load( BOOM, Sound.class);
            assetManager.load( OPPONENT_DIED, Sound.class);
            assetManager.load( JUST_DIED, Sound.class);
            assetManager.load( START_GAME, Sound.class);
            assetManager.load( MAIN_THEME, Sound.class);
        }
        assetManager.finishLoading();
    }

    public void unload(){
        assetManager.clear();
    }

    private boolean playSound( String name){
        return playSound( name, false);
    }

    private boolean playSound(final String name, final boolean isLoop){
        try {
        	Gdx.app.postRunnable(new Runnable() {
				
				@Override
				public void run() {
					Sound sound = assetManager.get( name, Sound.class);
		        	if ( isLoop){
		        		sound.loop();
		        	}
		        	else{
		        		sound.play();
		        	}
				}
			});
        	
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        return true;
    }

    private boolean playMusic( String name, boolean isLoop){
        try {
        	Music music = Gdx.app.getAudio().newMusic( Gdx.files.internal(name));
        	music.setLooping(isLoop);
        	music.play();
        	stopMusic();
        	activeMusic = music;
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        return true;
    }

    private void stopMusic(){
        if ( activeMusic != null){
            activeMusic.stop();
            activeMusic.dispose();
        }
        activeMusic = null;
    }

    public void playMainTheme() {
        playMusic(MAIN_THEME, true);
    }

    public void stopMainTheme() {
        stopMusic();
    }

    public void dropBomb() {
        playSound(DROP_BOMB);
    }

    public void boom() {
        playSound(BOOM);
    }

    public void playOpponentDying() {
        playSound(OPPONENT_DIED);
    }

    public void playJustDied() {
        playMusic(JUST_DIED, false);
    }

    public void playStartGame() {
        playMusic(START_GAME, false);
    }

}

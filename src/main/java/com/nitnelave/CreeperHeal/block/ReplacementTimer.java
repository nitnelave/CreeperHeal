package com.nitnelave.CreeperHeal.block;

import java.util.Date;

/**
 * Utility for all entities whose replacement is timed.
 * 
 * @author nitnelave
 * 
 */
public class ReplacementTimer {
    private Date time;
    private final boolean timed;
    private boolean postponed = false;

    /**
     * Constructor.
     * 
     * @param time
     *            The time at which the entity should be replaced.
     * @param timed
     *            Whether the replacement is in a world where replacements are
     *            timed, i.e. happen at a certain hour of the Minecraft day.
     */
    public ReplacementTimer (Date time, boolean timed) {
        this.time = time;
        this.timed = timed;
    }

    /**
     * Postpone the block's replacement.
     * 
     * @param delay
     *            The amount of time to postpone by, in milliseconds.
     * @return True if the block was postponed.
     */
    public boolean postPone (int delay) {
        if (postponed)
            return false;
        time = new Date (time.getTime () + 1000 * delay);
        postponed = true;
        return true;
    }

    /**
     * Get the recorded time.
     * 
     * @return The recorded time. Either the time the block was burnt or later
     *         if the replacement has been delayed.
     */
    public Date getTime () {
        return time;
    }

    /**
     * Check if the block should be repaired.
     * 
     * @return False if it is not time to replace it yet.
     */
    public boolean checkReplace () {
        return !timed && time.before (new Date ());
    }

    /**
     * Get whether the replacement is in a world with timed repairs (repairs at
     * a specific hour).
     * 
     * @return True if the replacement is timed.
     */
    public boolean isTimed () {
        return timed;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode () {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((time == null) ? 0 : time.hashCode ());
        result = prime * result + (timed ? 1231 : 1237);
        return result;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals (Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof ReplacementTimer))
            return false;
        ReplacementTimer other = (ReplacementTimer) obj;
        if (time == null)
        {
            if (other.time != null)
                return false;
        }
        else if (!time.equals (other.time))
            return false;
        return timed == other.timed;
    }

}

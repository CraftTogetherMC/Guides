package de.gcmclan.team.guides;

public class Segment implements Comparable < Segment > {
    private final int ID;
    private String txt;
    private boolean exitBySkip = false;
    private boolean spaceable = false;
    private int backSeg = -1;
    private int skipSeg = -1;

    public Segment(int ID, String txt) {
        this.ID = ID;
        this.txt = txt;
    }

    public Segment(int ID, String txt, boolean exitBySkip, boolean spaceable) {
        this(ID, txt);
        this.exitBySkip = exitBySkip;
        this.spaceable = spaceable;
    }

    public Segment(int ID, String txt, boolean exitBySkip, boolean spaceable, int backSeg, int skipSeg) {
        this(ID, txt, exitBySkip, spaceable);
        this.backSeg = backSeg;
        this.skipSeg = skipSeg;
    }

    public int compareTo(Segment seg) {
        if (this.ID > seg.ID) {
            return 1;
        }
        if (this.ID == seg.ID) {
            return 0;
        }

        return -1;
    }

    public boolean equals(Object obj) {
        if (obj instanceof Segment &&
                this.ID == ((Segment) obj).ID) {
            return true;
        }
        return false;
    }

    public int getID() {
        return this.ID;
    }

    public String getText() {
        return this.txt;
    }

    public boolean isExitBySkip() {
        return this.exitBySkip;
    }

    public boolean isSpaceable() {
        return this.spaceable;
    }

    public int getBackSeg() {
        return this.backSeg;
    }

    public int getSkipSeg() {
        return this.skipSeg;
    }

    public void setText(String txt) {
        this.txt = txt;
    }

    public void setExitBySkip(boolean exitBySkip) {
        this.exitBySkip = exitBySkip;
    }

    public void setSpaceable(boolean spaceable) {
        this.spaceable = spaceable;
    }

    public void setBackSeg(int backSeg) {
        this.backSeg = backSeg;
    }

    public void setSkipSeg(int skipSeg) {
        this.skipSeg = skipSeg;
    }
}
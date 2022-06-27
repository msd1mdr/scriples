package app.bmri.scripless.backing.useraccessmenu;

import org.apache.commons.codec.digest.DigestUtils;

public class GenUserPass {
    public GenUserPass() {
        super();
    }

    public static void main(String[] args) {
        GenUserPass genUserPass = new GenUserPass();
        System.out.println(DigestUtils.sha1Hex("welcome1"));
    }
}

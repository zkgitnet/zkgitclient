package se.miun.dt133g.zkgitclient.commands.login;

import se.miun.dt133g.zkgitclient.commands.Command;

public final class DecryptAesKey extends BaseCommandLogin implements Command {

    @Override public String execute() {
        rsaHandler.setRsaKey(credentials.getPrivRsa());
        rsaHandler.setInput(credentials.getEncAesKey());
        rsaHandler.decrypt();
        credentials.setAesKey(rsaHandler.getOutput());

        //System.out.println(rsaHandler.getOutput());
        return rsaHandler.getOutput();
    }
}

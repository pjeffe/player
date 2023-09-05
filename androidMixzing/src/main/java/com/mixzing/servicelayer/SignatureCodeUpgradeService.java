package com.mixzing.servicelayer;

import com.mixzing.message.messages.impl.ServerFileResponse;

public interface SignatureCodeUpgradeService {

    public void processUpdate();

    public void handleDownloadComplete(ServerFileResponse response);

}
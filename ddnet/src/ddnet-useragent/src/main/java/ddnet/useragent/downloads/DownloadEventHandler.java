package ddnet.useragent.downloads;


public interface DownloadEventHandler {
	void onNewDownload(Download download);
	void onUpdate(Download download);
	void onCompleted(Download download);
	void onFinishedWithErrors(Download download);
	void onCanceled(Download download);
}

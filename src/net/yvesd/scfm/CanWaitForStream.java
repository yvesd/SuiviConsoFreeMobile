package net.yvesd.scfm;

import java.util.List;

public interface CanWaitForStream {

	void addToProgress(int res, Object... args);

	void setProgressStatus(int progress);

	void handleResult(List<String> result);

}

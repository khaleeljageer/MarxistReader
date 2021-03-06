/*
 * Copyright (C) 2010-2015 FBReader.ORG Limited <contact@fbreader.org>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.android.fbreader;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import org.geometerplus.android.fbreader.dict.DictionaryUtil;
import org.geometerplus.android.fbreader.image.ImageViewActivity;
import org.geometerplus.android.fbreader.network.BookDownloader;
import org.geometerplus.android.fbreader.network.BookDownloaderService;
import org.geometerplus.android.fbreader.network.Util;
import org.geometerplus.android.fbreader.network.auth.ActivityNetworkContext;
import org.geometerplus.android.util.OrientationUtil;
import org.geometerplus.android.util.UIMessageUtil;
import org.geometerplus.fbreader.Paths;
import org.geometerplus.fbreader.bookmodel.FBHyperlinkType;
import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.fbreader.network.NetworkLibrary;
import org.geometerplus.fbreader.util.AutoTextSnippet;
import org.geometerplus.zlibrary.core.network.ZLNetworkException;
import org.geometerplus.zlibrary.text.view.*;

//处理文中的超链接：目录点击、脚注点击等等
class ProcessHyperlinkAction extends FBAndroidAction {
	ProcessHyperlinkAction(FBReader baseActivity, FBReaderApp fbreader) {
		super(baseActivity, fbreader);
	}

	@Override
	public boolean isEnabled() {
		return Reader.getTextView().getOutlinedRegion() != null;
	}

	@Override
	protected void run(Object ... params) {
		final ZLTextRegion region = Reader.getTextView().getOutlinedRegion();
		if (region == null) {
			return;
		}

		final ZLTextRegion.Soul soul = region.getSoul();
		if (soul instanceof ZLTextHyperlinkRegionSoul) {
			Reader.getTextView().hideOutline();
			Reader.getViewWidget().repaint();
			final ZLTextHyperlink hyperlink = ((ZLTextHyperlinkRegionSoul)soul).Hyperlink;
			switch (hyperlink.Type) {
				case FBHyperlinkType.EXTERNAL:
					openInBrowser(hyperlink.Id);
					break;
				case FBHyperlinkType.INTERNAL:
				case FBHyperlinkType.FOOTNOTE:
				{
					final AutoTextSnippet snippet = Reader.getFootnoteData(hyperlink.Id);
					if (snippet == null) {
						break;
					}

					Reader.Collection.markHyperlinkAsVisited(Reader.getCurrentBook(), hyperlink.Id);
					final boolean showToast;
					switch (Reader.MiscOptions.ShowFootnoteToast.getValue()) {
						default:
						case never:
							showToast = false;
							break;
						case footnotesOnly:
							showToast = hyperlink.Type == FBHyperlinkType.FOOTNOTE;
							break;
						case footnotesAndSuperscripts:
							showToast =
								hyperlink.Type == FBHyperlinkType.FOOTNOTE ||
								region.isVerticallyAligned();
							break;
						case allInternalLinks:
							showToast = true;
							break;
					}
					if (showToast) {
						if (snippet.IsEndOfText) {
							FBReader.toast(snippet.getText());
						} else {
							FBReader.toast(snippet.getText());
							Reader.getTextView().hideOutline();
							Reader.tryOpenFootnote(hyperlink.Id);
						}
						Reader.getTextView().hideOutline();
						Reader.getViewWidget().repaint();
					} else {
						Reader.tryOpenFootnote(hyperlink.Id);
					}
					break;
				}
			}
		} else if (soul instanceof ZLTextImageRegionSoul) {
			Reader.getTextView().hideOutline();
			Reader.getViewWidget().repaint();
			final String url = ((ZLTextImageRegionSoul)soul).ImageElement.URL;
			if (url != null) {
				try {
					final Intent intent = new Intent();
					intent.setClass(BaseActivity, ImageViewActivity.class);
					intent.putExtra(ImageViewActivity.URL_KEY, url);
					intent.putExtra(
						ImageViewActivity.BACKGROUND_COLOR_KEY,
						Reader.ImageOptions.ImageViewBackground.getValue().intValue()
					);
					OrientationUtil.startActivity(BaseActivity, intent);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else if (soul instanceof ZLTextWordRegionSoul) {
			DictionaryUtil.openTextInDictionary(
				BaseActivity,
				((ZLTextWordRegionSoul)soul).Word.getString(),
				true,
				region.getTop(),
				region.getBottom(),
				new Runnable() {
					public void run() {
						BaseActivity.outlineRegion(soul);
					}
				}
			);
		}
	}

	private void openInBrowser(final String url) {
		final Intent intent = new Intent(Intent.ACTION_VIEW);
		final boolean externalUrl;
		if (BookDownloader.acceptsUri(Uri.parse(url), null)) {
			intent.setClass(BaseActivity, BookDownloader.class);
			intent.putExtra(BookDownloaderService.Key.SHOW_NOTIFICATIONS, BookDownloaderService.Notifications.ALL);
			externalUrl = false;
		} else {
			externalUrl = true;
		}
		final NetworkLibrary nLibrary = NetworkLibrary.Instance(Paths.systemInfo(BaseActivity));
		new Thread(new Runnable() {
			public void run() {
				if (!url.startsWith("fbreader-action:")) {
					try {
						nLibrary.initialize(new ActivityNetworkContext(BaseActivity));
					} catch (ZLNetworkException e) {
						e.printStackTrace();
						UIMessageUtil.showMessageText(BaseActivity, e.getMessage());
						return;
					}
				}
				intent.setData(Util.rewriteUri(Uri.parse(nLibrary.rewriteUrl(url, externalUrl))));
				BaseActivity.runOnUiThread(new Runnable() {
					public void run() {
						try {
							OrientationUtil.startActivity(BaseActivity, intent);
						} catch (ActivityNotFoundException e) {
							e.printStackTrace();
						}
					}
				});
			}
		}).start();
	}
}

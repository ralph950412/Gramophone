package org.akanework.gramophone.ui.adapters

import android.os.Bundle
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import org.akanework.gramophone.R
import org.akanework.gramophone.logic.utils.MediaStoreUtils
import org.akanework.gramophone.ui.MainActivity
import org.akanework.gramophone.ui.fragments.GeneralSubFragment

/**
 * [PlaylistAdapter] is an adapter for displaying artists.
 */
class PlaylistAdapter(
    private val mainActivity: MainActivity,
    private val playlistList: MutableLiveData<MutableList<MediaStoreUtils.Playlist>>,
) : BaseAdapter<MediaStoreUtils.Playlist>
    (
    mainActivity,
    liveData = playlistList,
    sortHelper = StoreItemHelper(),
    naturalOrderHelper = null,
    initialSortType = Sorter.Type.ByTitleAscending,
    pluralStr = R.plurals.items,
    ownsView = true,
    defaultLayoutType = LayoutType.LIST
) {

    override val defaultCover = R.drawable.ic_default_cover_playlist

    override fun virtualTitleOf(item: MediaStoreUtils.Playlist): String {
        return context.getString(
            if (item is MediaStoreUtils.RecentlyAdded)
                R.string.recently_added else R.string.unknown_playlist
        )
    }

    override fun onClick(item: MediaStoreUtils.Playlist) {
        mainActivity.startFragment(
            GeneralSubFragment().apply {
                arguments =
                    Bundle().apply {
                        putInt("Position", toRawPos(item))
                        putInt("Item", R.id.playlist)
                    }
            },
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        super.onBindViewHolder(holder, position, payloads)
        if (playlistList.value!![position].title == context.getString(R.string.playlist_favourite)) {
            Glide
                .with(context)
                .load(R.drawable.ic_cover_favorite)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.songCover)
        }
    }

    override fun onMenu(item: MediaStoreUtils.Playlist, popupMenu: PopupMenu) {
        popupMenu.inflate(R.menu.more_menu_less)

        popupMenu.setOnMenuItemClickListener { it1 ->
            when (it1.itemId) {
                R.id.play_next -> {
                    val mediaController = mainActivity.getPlayer()
                    mediaController.addMediaItems(
                        mediaController.currentMediaItemIndex + 1,
                        item.songList,
                    )
                    true
                }

                else -> false
            }
        }
    }

}
package com.aero51.moviedatabase.utils;

import com.aero51.moviedatabase.repository.model.epg.EpgProgram;

public interface ProgramItemClickListener {
    void onItemClick(int position, int db_id, EpgProgram epgProgram);
}

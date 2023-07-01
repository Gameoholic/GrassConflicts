package com.github.gameoholic.grassconflicts.datatypes

import com.github.gameoholic.grassconflicts.enums.Phase

data class PhaseParameters(val duration: Int, val phase: Phase, val airDropMin: Int, val airDropMax: Int, var airDropDelay: Int)

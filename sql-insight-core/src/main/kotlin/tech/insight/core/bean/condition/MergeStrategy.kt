package tech.insight.core.bean.condition

import java.util.*
import tech.insight.core.engine.IdentifierSelectType


/**
 *
 * different merge strategy can merge different type IdentifierDetails
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
fun interface MergeStrategy {

    /**
     * merge two IdentifierDetails
     */
    fun merge(aDetails: IdentifierDetails, otherDetails: IdentifierDetails): IdentifierDetails


    companion object MergeStrategyFactory {

        private val strategyMap =
            EnumMap<IdentifierSelectType, EnumMap<IdentifierSelectType, MergeStrategy>>(IdentifierSelectType::class.java)

        init {
            val constMap = EnumMap<IdentifierSelectType, MergeStrategy>(IdentifierSelectType::class.java)
            constMap[IdentifierSelectType.CONST] = ConstMergeConst
            constMap[IdentifierSelectType.RANGE] = ConstMergeRange
            constMap[IdentifierSelectType.MULTI_CONST] = ConstMergeMultiConst
            strategyMap[IdentifierSelectType.CONST] = constMap

            val multiConstMap = EnumMap<IdentifierSelectType, MergeStrategy>(IdentifierSelectType::class.java)
            multiConstMap[IdentifierSelectType.CONST] = MultiConstMergeConst
            multiConstMap[IdentifierSelectType.RANGE] = MultiConstMergeRange
            multiConstMap[IdentifierSelectType.MULTI_CONST] = MultiConstMergeMultiConst
            strategyMap[IdentifierSelectType.MULTI_CONST] = multiConstMap

            val rangeMap = EnumMap<IdentifierSelectType, MergeStrategy>(IdentifierSelectType::class.java)
            rangeMap[IdentifierSelectType.CONST] = RangeMergeConst
            rangeMap[IdentifierSelectType.RANGE] = RangeMergeRange
            rangeMap[IdentifierSelectType.MULTI_CONST] = RangeMergeMultiConst
        }

        fun getMergeStrategy(type: IdentifierSelectType, otherType: IdentifierSelectType): MergeStrategy {
            if (type == IdentifierSelectType.IMPOSSIBLE || otherType == IdentifierSelectType.IMPOSSIBLE) {
                return ImpossibleMergeStrategy
            }
            return strategyMap[type]?.getValue(otherType)!!
        }
    }
}


object ImpossibleMergeStrategy : MergeStrategy {
    override fun merge(aDetails: IdentifierDetails, otherDetails: IdentifierDetails): IdentifierDetails {
        require(aDetails.name == otherDetails.name) {
            "merge IdentifierDetails with different name ${aDetails.name} and ${otherDetails.name}"
        }
        return ImpossibleIdentifierDetails(aDetails.name)
    }
}

object ConstMergeConst : MergeStrategy {
    override fun merge(aDetails: IdentifierDetails, otherDetails: IdentifierDetails): IdentifierDetails {
        require(aDetails.name == otherDetails.name) {
            "merge IdentifierDetails with different name ${aDetails.name} and ${otherDetails.name}"
        }
        if ((aDetails as ConstIdentifierDetails).value == (otherDetails as ConstIdentifierDetails).value) {
            return aDetails
        }
        return ImpossibleIdentifierDetails(aDetails.name)
    }
}

object ConstMergeMultiConst : MergeStrategy {

    override fun merge(aDetails: IdentifierDetails, otherDetails: IdentifierDetails): IdentifierDetails {
        require(aDetails.name == otherDetails.name) {
            "merge IdentifierDetails with different name ${aDetails.name} and ${otherDetails.name}"
        }
        val constDetails = aDetails as ConstIdentifierDetails
        if (constDetails.value in (otherDetails as MultiConstIdentifierDetails).points) {
            return constDetails
        }
        return ImpossibleIdentifierDetails(constDetails.name)
    }
}

object ConstMergeRange : MergeStrategy {

    override fun merge(aDetails: IdentifierDetails, otherDetails: IdentifierDetails): IdentifierDetails {
        require(aDetails.name == otherDetails.name) {
            "merge IdentifierDetails with different name ${aDetails.name} and ${otherDetails.name}"
        }
        val constDetails = aDetails as ConstIdentifierDetails
        val range = otherDetails as RangeIdentifierDetails
        if (constDetails.value in range.range) {
            return constDetails
        }
        return ImpossibleIdentifierDetails(constDetails.name)
    }

}

object MultiConstMergeConst : MergeStrategy {

    override fun merge(aDetails: IdentifierDetails, otherDetails: IdentifierDetails): IdentifierDetails {
        return ConstMergeMultiConst.merge(otherDetails, aDetails)
    }

}

object MultiConstMergeMultiConst : MergeStrategy {

    override fun merge(aDetails: IdentifierDetails, otherDetails: IdentifierDetails): IdentifierDetails {
        val points1 = (aDetails as MultiConstIdentifierDetails).points
        val points2 = (otherDetails as MultiConstIdentifierDetails).points
        val retainPoints = points1.intersect(points2.toSet())
        if (retainPoints.isEmpty()) {
            return ImpossibleIdentifierDetails(aDetails.name)
        }
        if (retainPoints.size == 1) {
            return ConstIdentifierDetails(aDetails.name, retainPoints.first())
        }
        return MultiConstIdentifierDetails(aDetails.name, retainPoints.toList())
    }
}

object MultiConstMergeRange : MergeStrategy {

    override fun merge(aDetails: IdentifierDetails, otherDetails: IdentifierDetails): IdentifierDetails {
        val points = (aDetails as MultiConstIdentifierDetails).points
        val range = (otherDetails as RangeIdentifierDetails).range
        val retain = points.filter { range.contains(it) }
        if (retain.isEmpty()) {
            return ImpossibleIdentifierDetails(aDetails.name)
        }
        if (retain.size == 1) {
            return ConstIdentifierDetails(aDetails.name, retain.first())
        }
        return MultiConstIdentifierDetails(aDetails.name, retain.toList())
    }
}

object RangeMergeConst : MergeStrategy {

    override fun merge(aDetails: IdentifierDetails, otherDetails: IdentifierDetails): IdentifierDetails {
        return ConstMergeRange.merge(otherDetails, aDetails)
    }

}

object RangeMergeMultiConst : MergeStrategy {

    override fun merge(aDetails: IdentifierDetails, otherDetails: IdentifierDetails): IdentifierDetails {
        return MultiConstMergeRange.merge(otherDetails, aDetails)
    }

}

object RangeMergeRange : MergeStrategy {

    override fun merge(aDetails: IdentifierDetails, otherDetails: IdentifierDetails): IdentifierDetails {
        require(aDetails is RangeIdentifierDetails) {
            "details is not RangeIdentifierDetails"
        }
        require(otherDetails is RangeIdentifierDetails) {
            "details is not RangeIdentifierDetails"
        }
        val newRange = aDetails.range.union(otherDetails.range)
        if (newRange.type == RangeType.IMPOSSIBLE) {
            return ImpossibleIdentifierDetails(aDetails.name)
        }
        if (newRange.unique()) {
            return ConstIdentifierDetails(aDetails.name, newRange.start)
        }
        return RangeIdentifierDetails(aDetails.name, newRange)
    }
}

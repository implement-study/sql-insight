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
    fun merge(details1: IdentifierDetails, details2: IdentifierDetails): IdentifierDetails


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
    override fun merge(details1: IdentifierDetails, details2: IdentifierDetails): IdentifierDetails {
        require(details1.name == details2.name) {
            "merge IdentifierDetails with different name ${details1.name} and ${details2.name}"
        }
        return ImpossibleIdentifierDetails(details1.name)
    }
}

object ConstMergeConst : MergeStrategy {
    override fun merge(constDetails: IdentifierDetails, otherDetails: IdentifierDetails): IdentifierDetails {
        require(constDetails.name == otherDetails.name) {
            "merge IdentifierDetails with different name ${constDetails.name} and ${otherDetails.name}"
        }
        if ((constDetails as ConstIdentifierDetails).value == (otherDetails as ConstIdentifierDetails).value) {
            return constDetails
        }
        return ImpossibleIdentifierDetails(constDetails.name)
    }
}

object ConstMergeMultiConst : MergeStrategy {

    override fun merge(details1: IdentifierDetails, details2: IdentifierDetails): IdentifierDetails {
        require(details1.name == details2.name) {
            "merge IdentifierDetails with different name ${details1.name} and ${details2.name}"
        }
        val constDetails = details1 as ConstIdentifierDetails
        if (constDetails.value in (details2 as MultiConstIdentifierDetails).points) {
            return constDetails
        }
        return ImpossibleIdentifierDetails(constDetails.name)
    }
}

object ConstMergeRange : MergeStrategy {

    override fun merge(details1: IdentifierDetails, details2: IdentifierDetails): IdentifierDetails {
        require(details1.name == details2.name) {
            "merge IdentifierDetails with different name ${details1.name} and ${details2.name}"
        }
        val constDetails = details1 as ConstIdentifierDetails
        val range = details2 as RangeIdentifierDetails
        if (constDetails.value in range.range) {
            return constDetails
        }
        return ImpossibleIdentifierDetails(constDetails.name)
    }

}

object MultiConstMergeConst : MergeStrategy {

    override fun merge(details1: IdentifierDetails, details2: IdentifierDetails): IdentifierDetails {
        return ConstMergeMultiConst.merge(details2, details1)
    }

}

object MultiConstMergeMultiConst : MergeStrategy {

    override fun merge(details1: IdentifierDetails, details2: IdentifierDetails): IdentifierDetails {
        val points1 = (details1 as MultiConstIdentifierDetails).points
        val points2 = (details2 as MultiConstIdentifierDetails).points
        val retainPoints = points1.intersect(points2.toSet())
        if (retainPoints.isEmpty()) {
            return ImpossibleIdentifierDetails(details1.name)
        }
        if (retainPoints.size == 1) {
            return ConstIdentifierDetails(details1.name, retainPoints.first())
        }
        return MultiConstIdentifierDetails(details1.name, retainPoints.toList())
    }
}

object MultiConstMergeRange : MergeStrategy {

    override fun merge(details1: IdentifierDetails, details2: IdentifierDetails): IdentifierDetails {
        val points = (details1 as MultiConstIdentifierDetails).points
        val range = (details2 as RangeIdentifierDetails).range
        val retain = points.filter { range.contains(it) }
        if (retain.isEmpty()) {
            return ImpossibleIdentifierDetails(details1.name)
        }
        if (retain.size == 1) {
            return ConstIdentifierDetails(details1.name, retain.first())
        }
        return MultiConstIdentifierDetails(details1.name, retain.toList())
    }
}

object RangeMergeConst : MergeStrategy {

    override fun merge(details1: IdentifierDetails, details2: IdentifierDetails): IdentifierDetails {
        return ConstMergeRange.merge(details2, details1)
    }

}

object RangeMergeMultiConst : MergeStrategy {

    override fun merge(details1: IdentifierDetails, details2: IdentifierDetails): IdentifierDetails {
        return MultiConstMergeRange.merge(details2, details1)
    }

}

object RangeMergeRange : MergeStrategy {

    override fun merge(details1: IdentifierDetails, details2: IdentifierDetails): IdentifierDetails {
        val range1 = (details1 as RangeIdentifierDetails).range
        val range2 = (details2 as RangeIdentifierDetails).range
        val start = maxOf(range1.start, range2.start)
        val end = minOf(range1.endExclusive, range2.endExclusive)
        return ImpossibleIdentifierDetails(details1.name)
        //        if (start < end) {
        //           // OpenEndRange<Value<*>>(start,end)
        //          //  return RangeIdentifierDetails(details1.name, start..end)
        //        }
        //
        //
        //        val retain = range1.intersect(range2)
        //        if (retain.isEmpty()) {
        //            return ImpossibleIdentifierDetails(details1.name)
        //        }
        //        if (retain.size == 1) {
        //            return ConstIdentifierDetails(details1.name, retain.first())
        //        }
        //        return RangeIdentifierDetails(details1.name, retain)
    }
}


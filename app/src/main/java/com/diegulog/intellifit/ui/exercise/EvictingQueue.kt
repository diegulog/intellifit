package com.diegulog.intellifit.ui.exercise

import com.diegulog.intellifit.domain.entity.Sample
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Solo guarda elementos que se encuentran dentro de un rango de tiempo maximo en milllis
 */
class EvictingQueue(private val maxTimestamp: Int): ConcurrentLinkedQueue<Sample>() {

    override fun add(element: Sample): Boolean {
        for (sample in this) {
            if(sample.timestamp < System.currentTimeMillis() - maxTimestamp){
                this.remove(sample)
            }else{
                break
            }
        }
        return super.add(element)
    }

}
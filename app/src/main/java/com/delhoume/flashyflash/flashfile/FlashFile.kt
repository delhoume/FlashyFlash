package com.delhoume.flashyflash.flashfile
import java.util.regex.Pattern

class FlashFile {
    inner class Span {
        constructor(city: String, order: Int, start: Int) {
            this.city = city
            this.start = start
            this.order = order
            this.spanlen = 0
        }

        var city: String = ""
        var start: Int = 0
        var spanlen: Int
        var order: Int = 0
    }

    var current_city_code: String = "PA"
    var previous_full_city: String = ""

    // could use StringTokenizer
    @Throws(NullPointerException::class)
    fun tokenize(contents: String): MutableList<String> {
        val tokens: MutableList<String> = ArrayList()
        if (!contents.isEmpty()) {
            val lines = contents.split("\n")
            for (l in lines.indices) {
                var line = lines[l]
                val comment_pos = line.indexOf('#')
                if (comment_pos != -1) line = line.substring(0, comment_pos)
                val words = line.split(" ")
                for (w in words.indices) {
                    val good_word = words[w].trim()
                    if (!good_word.isEmpty()) {
                        tokens.add(good_word)
                    }
                }
            }
        }
        return tokens
    }

    fun decodeString(contents: String): MutableList<String> {
        val tokens = this.tokenize(contents)
        return this.decode(tokens)
    }

    fun decode(tokens: List<String>): MutableList<String> {
        val current_list: MutableList<String> = ArrayList()
        this.current_city_code = "PA"
        for (c in tokens.indices) {
            val command = tokens[c]
            if (command.contains("_")) {
                val parts =
                    command.split("_")
                this.current_city_code = parts[0]
                handleOrderToken(parts[1].trim(), current_list)
            } else {
                handleOrderToken(command.trim(), current_list)
            }
        }
        return current_list
    }

    fun emitOrderToken(current_span: Span, use_invader_numbers: Boolean): String {
        if (current_span.spanlen == 0) return printInvaderNumber(
            current_span.start,
            use_invader_numbers
        )
        else if (current_span.spanlen == 1) return "" + printInvaderNumber(
            current_span.start,
            use_invader_numbers
        ) + "+"
        else {
            val absolute = printInvaderNumber(
                current_span.start,
                use_invader_numbers
            ) + "," + printInvaderNumber(current_span.start + current_span.spanlen, use_invader_numbers)
            val relative =
                printInvaderNumber(current_span.start, use_invader_numbers) + "+" + current_span.spanlen
            return if (relative.length > absolute.length) absolute else relative
        }
    }

    fun emitFullToken(
        current_span: Span,
        needs_city: Boolean,
        use_invader_numbers: Boolean
    ): String {
        return (if (needs_city) (current_span.city + "_") else "") + emitOrderToken(
            current_span,
            use_invader_numbers
        )
    }

    fun encodeString(
        contents: String,
        keep_order: Boolean,
        use_invader_numbers: Boolean
    ): List<String> {
        val tokens = this.tokenize(contents)
        return encode(tokens, keep_order, use_invader_numbers)
    }

    fun encode(
        tokens: List<String>,
        keep_order: Boolean,
        use_invader_numbers: Boolean
    ): List<String> {
        val encoded: MutableList<String> = ArrayList()
        if (tokens.size == 0) return encoded
        // split all to allow sorting and fill input structure by city
        val sis: MutableList<Span> = ArrayList()
        for (string in tokens) {
            val parts = string.split("_")
            if (parts.size == 2) {
                val city = parts[0]
                val order = parts[1].toInt()
                sis.add(Span(city, order, 0))
            }
        }
        // if sorting is allowed
        if (!keep_order) {
            //            console.log("sorting");
            /*
            Arrays.sort(sis,new Comparator<Span>() {
                @Override
                public int compare(Span o1, Span o2) {
                    if (o1.city.equals(o2.city))
                        return o1.order - o2.order;
                    return o1.city.compareTo(o2.city);
                }
            });
            */
        } else {
            //    console.log("no sorting");
        }
        val the_spans = sis
        val current_span: Span = Span(the_spans[0].city,  0, the_spans[0].order)
        val previous_full_city = "None"
        for (s in 1 until the_spans.size) {
            val si = the_spans[s]
            //           console.log("considering", si, current_span.city, current_span.start, current_span.spanlen);
            if ((si.city == current_span.city) && si.order == (current_span.start + current_span.spanlen + 1)) {
                current_span.spanlen += 1
            } else if ((si.city != current_span.city) || (si.order != (current_span.start + current_span.spanlen + 1))) {
                //                   console.log("different city or same city but not neighbours")
                val token = emitFullToken(
                    current_span,
                    previous_full_city != current_span.city,
                    use_invader_numbers
                )
                encoded.add(token)
                this.previous_full_city = current_span.city
                current_span.city = si.city
                current_span.start = si.order
                current_span.spanlen = 0
            }
        }

        //      console.log("flush");
        encoded.add(
            emitFullToken(
                current_span,
                previous_full_city != current_span.city,
                use_invader_numbers))
        //      console.log(encoded);
        return encoded
    }


    fun handleOrderToken(order: String, current_list: MutableList<String>) {
        if (order.isEmpty()) return
        val start: Int
        val end: Int
        if (order.contains(",")) { // we have an absolut range
            val bounds = order.split(Pattern.quote(","))
            start = bounds[0].toInt()
            end = bounds[1].toInt()
            if (end >= start) handleRelativeOrderRange(start, end - start + 1, current_list)
        } else if (order.contains("+")) {
            val bounds = order.split("+")
            if (bounds.size == 1) {
                start = bounds[0].toInt()
                handleRelativeOrderRange(start, 2, current_list)
            } else if (bounds.size == 2) {
                start = bounds[0].toInt()
                val len = if (bounds[1].isEmpty()) 1 else bounds[1].toInt()
                handleRelativeOrderRange(start, len + 1, current_list)
            }
        } else { // single number
            val num = order.toInt()
            handleRelativeOrderRange(num, 1, current_list)
        }
    }

    fun handleRelativeOrderRange(r1: Int, len: Int, current_list: MutableList<String>) {
        for (l in 0 until len) {
            val full_code = this.current_city_code + "_" + printInvaderNumber(r1 + l, true)
            if (!current_list.contains(full_code)) current_list.add(full_code)
        }
    }
}
// use_invader_numbers allows to squeeze one char for orders < 10
fun printInvaderNumber(num: Int, use_invader_numbers: Boolean): String {
    val res = num.toString()
    return if ((num < 10 && use_invader_numbers)) "0$res" else res
}


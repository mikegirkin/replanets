object Runner {
    def main(args: Array[String]) = {
        val map = replanets.common.Map.readFromXyplan("sdfsdf")
        println(map.toList)
    }
}


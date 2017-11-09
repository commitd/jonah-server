import * as React from 'react'
import { VictoryPie, VictoryTheme } from 'victory'

interface Props {
    data: {
        x: string,
        y: number
    }[]
}

// TODO: The padding here is a hack for the label being outside the area (ie chart is too big...)

class SimplePieChart extends React.Component<Props> {
    render() {
        const { data } = this.props
        return (
            <VictoryPie
                theme={VictoryTheme.material}
                data={data}
                colorScale="qualitative"
            />
        )
    }
}

export default SimplePieChart

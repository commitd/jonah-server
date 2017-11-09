import * as React from 'react'
import { VictoryChart, VictoryLine, VictoryTheme } from 'victory'
import sizeMe, { SizeMeProps } from 'react-sizeme'

interface OwnProps {
    data: {
        x: Date,
        y: number
    }[]
}

type Props = OwnProps & SizeMeProps

// TODO: Max height... ? the component keeps growing!! by +4 pixels each time

class TimelineChart extends React.Component<Props> {
    render() {
        const { data } = this.props
        return (
            <VictoryChart
                theme={VictoryTheme.material}
                scale={{ x: 'time' }}
                standalone={true}
                width={this.props.size.width || 1}
                height={500}
            >
                <VictoryLine data={data} />
            </VictoryChart>
        )
    }
}

// TODO: Could add brushing here. See the Victory example.

export default sizeMe<OwnProps>({ monitorHeight: true })(TimelineChart)

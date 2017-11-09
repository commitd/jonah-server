import * as React from 'react'
import { VictoryChart, VictoryBar, VictoryTheme } from 'victory'
import sizeMe, { SizeMeProps } from 'react-sizeme'

interface OwnProps {
    data: {
        x: string | number,
        y: number
    }[]
}

type Props = OwnProps & SizeMeProps

// TODO: Max height... ? the component keeps growing!! by +4 pixels each time

class BarChart extends React.Component<Props> {
    render() {
        const { data } = this.props
        return (
            <VictoryChart
                theme={VictoryTheme.material}
                domainPadding={10}
                standalone={true}
                width={this.props.size.width || 1}
                height={600}
            >
                <VictoryBar data={data} horizontal={true} />
            </VictoryChart>
        )
    }
}

// TODO: Could add brushing here. See the Victory example.

export default sizeMe<OwnProps>({ monitorHeight: true })(BarChart)


declare module 'react-sizeme' {
    import * as React from 'react'

    export type SizeMeProps = {
        size: {
            width?: number,
            height?: number,
            position?: {
                left: number,
                top: number,
                right: number,
                bottom: number
            }
        }
    }

    export type SizeMeConfig = {
        // If true, then any changes to your Components rendered width will cause an
        // recalculation of the "size" prop which will then be be passed into
        // your Component.
        monitorWidth?: boolean,

        // If true, then any changes to your Components rendered height will cause an
        // recalculation of the "size" prop which will then be be passed into
        // your Component.
        monitorHeight: boolean,

        // NOTE: This currently does not work, apologies. You will get a value if
        // you set this to true, but if only your components position changes and 
        // not its size then you will not get an updated position. I need to 
        // look deeper into this to find a performant solution. 
        // If true, then any changes to your Components position will cause an
        // recalculation of the "size" prop which will then be be passed into
        // your Component.
        monitorPosition?: boolean,

        // The maximum frequency, in milliseconds, at which size changes should be
        // recalculated when changes in your Component's rendered size are being
        // detected. This should not be set to lower than 16.
        refreshRate?: number,

        // The mode in which refreshing should occur.  Valid values are "debounce"
        // and "throttle".  "throttle" will eagerly measure your component and then
        // wait for the refreshRate to pass before doing a new measurement on size
        // changes. "debounce" will wait for a minimum of the refreshRate before
        // it does a measurement check on your component.  "debounce" can be useful
        // in cases where your component is animated into the DOM.
        // NOTE: When using "debounce" mode you may want to consider disabling the
        // placeholder as this adds an extra delay in the rendering time of your
        // component.
        refreshMode?: 'throttle' | 'measurement',

        // By default we render a "placeholder" component initially so we can try
        // and "prefetch" the expected size for your component.  This is to avoid
        // any unnecessary deep tree renders.  If you feel this is not an issue
        // for your component case and you would like to get an eager render of
        // your component then disable the placeholder using this config option.
        // NOTE: You can set this globally. See the docs on first render.
        noPlaceholder?: boolean
    }

    export type SizeMeWrapper<Props> = (component: React.ComponentType<Props & SizeMeProps>) => React.ComponentType<Props>
    export default function <Props>(config?: SizeMeConfig): SizeMeWrapper<Props>
}